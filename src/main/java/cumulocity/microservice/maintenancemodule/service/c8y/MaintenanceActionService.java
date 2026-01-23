package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.alarm.AlarmFilter;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import c8y.RequiredAvailability;
import cumulocity.microservice.maintenancemodule.model.MaintenanceAction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MaintenanceActionService {
    
    private static final String EVENT_TYPE_MAINTENANCE = "c8y_MaintenanceActionEvent";

    private static final String FRAGMENT_STATUS_MAINTENANCE = "ma_Status";

    private InventoryApi inventoryApi;

    private EventApi eventApi;

    private AlarmApi alarmApi;

    public MaintenanceActionService(InventoryApi inventoryApi, EventApi eventApi, AlarmApi alarmApi) {
        this.inventoryApi = inventoryApi;
        this.eventApi = eventApi;
        this.alarmApi = alarmApi;
    }

    public MaintenanceAction createMaintenanceAction(MaintenanceAction maintenanceAction) {
        log.info("Creating maintenance action: {}", maintenanceAction);
        switch (maintenanceAction.getStatus()) {
            case SCHEDULED:
                log.info("Maintenance action scheduled for device: {}", maintenanceAction.getDeviceId());
                return createScheduledAction(maintenanceAction);
            case IN_PROGRESS:
                log.info("Maintenance action in progress for device: {}", maintenanceAction.getDeviceId());
                return createInProgressAction(maintenanceAction);
            case COMPLETED:
                log.info("Maintenance action completed for device: {}", maintenanceAction.getDeviceId());
                return createCompletedAction(maintenanceAction);
            case CANCELLED:
                log.info("Maintenance action cancelled for device: {}", maintenanceAction.getDeviceId());
                return createCancelledAction(maintenanceAction);
            default:
                log.warn("Unknown maintenance action status: {}", maintenanceAction.getStatus());    
                return null;
        }
    }

    private MaintenanceAction createScheduledAction(MaintenanceAction maintenanceAction) {
        createEvent(maintenanceAction);
        updateMaintenanceAlarm(maintenanceAction.getDeviceId(), CumulocityAlarmStatuses.ACTIVE, CumulocityAlarmStatuses.ACKNOWLEDGED);
        return maintenanceAction;
    }

    private MaintenanceAction createInProgressAction(MaintenanceAction maintenanceAction) {
        createEvent(maintenanceAction);
        updateMaintenanceMode(maintenanceAction.getDeviceId(), true);
        updateMaintenanceAlarm(maintenanceAction.getDeviceId(), CumulocityAlarmStatuses.ACKNOWLEDGED, CumulocityAlarmStatuses.ACKNOWLEDGED);
        return maintenanceAction;
    }

    private MaintenanceAction createCompletedAction(MaintenanceAction maintenanceAction) {
        createEvent(maintenanceAction);
        updateMaintenanceMode(maintenanceAction.getDeviceId(), false);
        updateMaintenanceAlarm(maintenanceAction.getDeviceId(), CumulocityAlarmStatuses.ACKNOWLEDGED, CumulocityAlarmStatuses.CLEARED);
        return maintenanceAction;
    }

    private MaintenanceAction createCancelledAction(MaintenanceAction maintenanceAction) {
        createEvent(maintenanceAction);
        updateMaintenanceMode(maintenanceAction.getDeviceId(), false);
        updateMaintenanceAlarm(maintenanceAction.getDeviceId(), CumulocityAlarmStatuses.ACKNOWLEDGED, CumulocityAlarmStatuses.ACTIVE);
        return maintenanceAction;
    }

    private void createEvent(MaintenanceAction maintenanceAction) {
        ManagedObjectRepresentation device = new ManagedObjectRepresentation();
        device.setId(new GId(maintenanceAction.getDeviceId()));
        
        EventRepresentation event = new EventRepresentation();
        event.setDateTime(new DateTime());
        event.setText(maintenanceAction.getNotification() != null ? maintenanceAction.getNotification() : "Maintenance action: " + maintenanceAction.getStatus());
        event.setType(EVENT_TYPE_MAINTENANCE);
        event.setSource(device);
        event.set(maintenanceAction.getStatus().name(), FRAGMENT_STATUS_MAINTENANCE);
        
        try {
            eventApi.create(event);
        } catch (SDKException e) {
            log.error("Error creating event for device: {}", maintenanceAction.getDeviceId(), e);
        }
    }

    private void updateMaintenanceMode(String deviceId, boolean maintenanceMode) {
        try {
            ManagedObjectRepresentation currentDevice = inventoryApi.get(GId.asGId(deviceId)); 
            RequiredAvailability currentRequiredAvailability = currentDevice.get(RequiredAvailability.class);
            if(currentRequiredAvailability == null) {
                log.warn("Device does not have RequiredAvailability fragment, cannot update maintenance mode for device: {}", deviceId);
                return;
            }
            int currentResponseInterval = currentRequiredAvailability.getResponseInterval();
            ManagedObjectRepresentation device = new ManagedObjectRepresentation();
            device.setId(new GId(deviceId));
            RequiredAvailability availability = new RequiredAvailability();
            if(maintenanceMode) {
                availability.setResponseInterval(-Math.abs(currentResponseInterval));
            } else {
                availability.setResponseInterval(Math.abs(currentResponseInterval));
            }
            device.set(availability);
            inventoryApi.update(device);
        } catch (SDKException e) {
            log.error("Error updating maintenance mode for device: {}", deviceId, e);
        }
    }

    private void updateMaintenanceAlarm(String deviceId, CumulocityAlarmStatuses fromStatus, CumulocityAlarmStatuses toStatus) {
        try {
            AlarmFilter alarmFilter = new AlarmFilter();
            alarmFilter.bySource(GId.asGId(deviceId)).byStatus(fromStatus).byType(MaintenancePlanMapper.ALARM_TYPE);

            alarmApi.getAlarmsByFilter(alarmFilter).get(1).forEach(alarm -> {
                alarm.setStatus(toStatus.name());
                alarmApi.update(alarm);
            });
        } catch (SDKException e) {
            log.error("Error updating maintenance alarm for device: {}", deviceId, e);
        }
    }

}
