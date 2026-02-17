package cumulocity.microservice.maintenancemodule.service.c8y;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.alarm.AlarmFilter;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import cumulocity.microservice.maintenancemodule.model.RequiredAvailability; // ADD THIS
import cumulocity.microservice.maintenancemodule.model.MaintenanceAction;
import cumulocity.microservice.maintenancemodule.model.MaintenanceAction.MaintenanceStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MaintenanceActionService {

    // Local constants to ensure compilation without modifying Mapper
    private static final String EVENT_TYPE_MAINTENANCE = "c8y_MaintenanceActionEvent";
    private static final String FRAGMENT_STATUS_MAINTENANCE = "ma_Status";
    private static final String DEVICE_LAST_MAINTENANCE = "c8y_LastMaintenance";
    private static final String ALARM_TYPE = "c8y_MaintenanceAlarm";

    private final InventoryApi inventoryApi;
    private final EventApi eventApi;
    private final AlarmApi alarmApi;

    public MaintenanceActionService(InventoryApi inventoryApi, EventApi eventApi, AlarmApi alarmApi) {
        this.inventoryApi = inventoryApi;
        this.eventApi = eventApi;
        this.alarmApi = alarmApi;
    }

    public MaintenanceAction createMaintenanceAction(MaintenanceAction maintenanceAction, MaintenancePlan maintenancePlan) {
        log.info("Creating maintenance action: {}", maintenanceAction);

        if (maintenanceAction.getStatus() == null) {
            log.warn("Maintenance action has no status");
            return null;
        }

        switch (maintenanceAction.getStatus()) {
            case SCHEDULED:
                log.info("Maintenance action scheduled for device: {}", maintenanceAction.getDeviceId());
                return createScheduledAction(maintenancePlan, maintenanceAction);
            case IN_PROGRESS:
                log.info("Maintenance action in progress for device: {}", maintenanceAction.getDeviceId());
                return createInProgressAction(maintenancePlan, maintenanceAction);
            case COMPLETED:
                log.info("Maintenance action completed for device: {}", maintenanceAction.getDeviceId());
                return createCompletedAction(maintenancePlan, maintenanceAction);
            case CANCELLED:
                log.info("Maintenance action cancelled for device: {}", maintenanceAction.getDeviceId());
                return createCancelledAction(maintenancePlan, maintenanceAction);
            default:
                log.warn("Unknown maintenance action status: {}", maintenanceAction.getStatus());
                return null;
        }
    }

    private MaintenanceAction createScheduledAction(MaintenancePlan maintenancePlan, MaintenanceAction maintenanceAction) {
        createEvent(maintenanceAction, maintenancePlan);
        updateMaintenanceAlarm(maintenanceAction.getDeviceId(), maintenancePlan, CumulocityAlarmStatuses.ACTIVE, CumulocityAlarmStatuses.ACKNOWLEDGED);
        return maintenanceAction;
    }

    private MaintenanceAction createInProgressAction(MaintenancePlan maintenancePlan, MaintenanceAction maintenanceAction) {
        createEvent(maintenanceAction, maintenancePlan);
        updateMaintenanceMode(maintenanceAction.getDeviceId(), maintenanceAction.getStatus(), maintenancePlan);
        updateMaintenanceAlarm(maintenanceAction.getDeviceId(), maintenancePlan, CumulocityAlarmStatuses.ACKNOWLEDGED, CumulocityAlarmStatuses.ACKNOWLEDGED);
        return maintenanceAction;
    }

    private MaintenanceAction createCompletedAction(MaintenancePlan maintenancePlan, MaintenanceAction maintenanceAction) {
        createEvent(maintenanceAction, maintenancePlan);
        updateMaintenanceMode(maintenanceAction.getDeviceId(), maintenanceAction.getStatus(), maintenancePlan);
        updateMaintenanceAlarm(maintenanceAction.getDeviceId(), maintenancePlan, CumulocityAlarmStatuses.ACKNOWLEDGED, CumulocityAlarmStatuses.CLEARED);
        return maintenanceAction;
    }

    private MaintenanceAction createCancelledAction(MaintenancePlan maintenancePlan, MaintenanceAction maintenanceAction) {
        createEvent(maintenanceAction, maintenancePlan);
        updateMaintenanceMode(maintenanceAction.getDeviceId(), maintenanceAction.getStatus(), maintenancePlan);
        updateMaintenanceAlarm(maintenanceAction.getDeviceId(), maintenancePlan, CumulocityAlarmStatuses.ACKNOWLEDGED, CumulocityAlarmStatuses.ACTIVE);
        return maintenanceAction;
    }

    private void createEvent(MaintenanceAction maintenanceAction, MaintenancePlan maintenancePlan) {
        ManagedObjectRepresentation device = new ManagedObjectRepresentation();
        device.setId(new GId(maintenanceAction.getDeviceId()));

        EventRepresentation event = new EventRepresentation();
        event.setDateTime(new DateTime());
        event.setText("Maintenance Plan: " + maintenancePlan.getName() + ", Action: " + maintenanceAction.getStatus());
        event.setType(MaintenancePlanMapper.EVENT_TYPE_MAINTENANCE+maintenancePlan.getId());
        event.setSource(device);
        event.set(maintenanceAction.getStatus().name(), FRAGMENT_STATUS_MAINTENANCE);

        event.set(maintenanceAction.getStatus().name(), MaintenancePlanMapper.FRAGMENT_STATUS_MAINTENANCE);

        try {
            eventApi.create(event);
        } catch (SDKException e) {
            log.error("Error creating event for device: {}", maintenanceAction.getDeviceId(), e);
        }
    }

    private void updateMaintenanceMode(String deviceId, MaintenanceStatus status, MaintenancePlan maintenancePlan) {
        try {
            ManagedObjectRepresentation currentDevice = inventoryApi.get(GId.asGId(deviceId));
            RequiredAvailability currentRequiredAvailability = currentDevice.get(RequiredAvailability.class);

            ManagedObjectRepresentation deviceUpdate = new ManagedObjectRepresentation();
            deviceUpdate.setId(new GId(deviceId));
            boolean needUpdate = false;

            if(currentRequiredAvailability != null) {
                log.warn("Device does not have RequiredAvailability fragment, cannot update maintenance mode for device: {}", deviceId);
                int currentResponseInterval = currentRequiredAvailability.getResponseInterval();
                RequiredAvailability availability = new RequiredAvailability();

                if (MaintenanceStatus.IN_PROGRESS.equals(status)) {
                    // Set negative interval to indicate maintenance mode (standard C8Y pattern)
                    availability.setResponseInterval(-Math.abs(currentResponseInterval));
                } else {
                    availability.setResponseInterval(Math.abs(currentResponseInterval));
                }
                deviceUpdate.set(availability);
                needUpdate = true;
            } else {
                log.warn("Device {} does not have RequiredAvailability fragment, skipping maintenance mode update", deviceId);
            }

            if (MaintenanceStatus.COMPLETED.equals(status)) {
                log.info("Setting last maintenance date for device: {}", deviceId);
                deviceUpdate.set(new DateTime(), DEVICE_LAST_MAINTENANCE);
                needUpdate = true;
            }

            if (needUpdate) {
                inventoryApi.update(deviceUpdate);
            }
        } catch (SDKException e) {
            log.error("Error updating maintenance mode for device: {}", deviceId, e);
        }
    }

    private void updateMaintenanceAlarm(String deviceId, MaintenancePlan maintenancePlan, CumulocityAlarmStatuses fromStatus, CumulocityAlarmStatuses toStatus) {
        try {
            AlarmFilter alarmFilter = new AlarmFilter();
            alarmFilter.bySource(GId.asGId(deviceId))
                    .byStatus(fromStatus)
                    .byType(ALARM_TYPE);
            alarmFilter.bySource(GId.asGId(deviceId)).byStatus(fromStatus).byType(MaintenancePlanMapper.ALARM_TYPE+maintenancePlan.getId());

            // Corrected: Added .getAlarms() to iterate correctly over the paged result
            alarmApi.getAlarmsByFilter(alarmFilter)
                    .get(20)
                    .getAlarms()
                    .forEach(alarm -> {
                        alarm.setStatus(toStatus.name());
                        alarmApi.update(alarm);
                    });
        } catch (SDKException e) {
            log.error("Error updating maintenance alarm for device: {}", deviceId, e);
        }
    }

}
