package cumulocity.microservice.maintenancemodule.service.c8y;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;

@Service
public class TimeBasedMaintenanceService {
    private static final Logger log = LoggerFactory.getLogger(TimeBasedMaintenanceService.class);

    private final ContextService<MicroserviceCredentials> contextService;

    private final MaintenancePlanService maintenancePlanService;

    private final MaintenancePlanApplyService maintenancePlanApplyService;

    private final InventoryApi inventoryApi;

    private final AlarmApi alarmApi;

    @Autowired
    public TimeBasedMaintenanceService(ContextService<MicroserviceCredentials> contextService, MaintenancePlanService maintenancePlanService, MaintenancePlanApplyService maintenancePlanApplyService, InventoryApi inventoryApi, AlarmApi alarmApi) {
        this.contextService = contextService;
        this.maintenancePlanService = maintenancePlanService;
        this.maintenancePlanApplyService = maintenancePlanApplyService;
        this.inventoryApi = inventoryApi;
        this.alarmApi = alarmApi;
    }

    public Boolean runJobWithinContext(MicroserviceCredentials context) {
        Boolean callWithinContext = contextService.callWithinContext(context, (Callable<Boolean>) () -> {
            try {
                log.info("Start time-based maintenance job for tenant {}", context.getTenant());
                List<MaintenancePlan> maintenancePlans = maintenancePlanService.getAllTimeBasedMaintenancePlans();
                log.info("Found {} active time-based maintenance plans", maintenancePlans.size());
                for (MaintenancePlan maintenancePlan : maintenancePlans) {
                    MaintenancePlan maintenancePlanUpdated = checkAndUpdateActivateFlag(maintenancePlan);
                    if(maintenancePlanUpdated.getActive() == null || !maintenancePlanUpdated.getActive()) {
                        log.info("Skipping inactive maintenance plan {}", maintenancePlan.getName());
                        continue;
                    }
                    Set<ManagedObjectRepresentation> devices = maintenancePlanApplyService.getAllAppliedDevices(maintenancePlanUpdated);
                    log.info("Maintenance plan {} applies to {} devices", maintenancePlanUpdated.getName(), devices.size());
                    for (ManagedObjectRepresentation device : devices) {
                        switch(maintenancePlanUpdated.getNotificationClass()) {
                            case ALARM:
                                log.info("Processing device {} for maintenance plan {} with ALARM notification class", device.getId().getValue(), maintenancePlanUpdated.getName());
                                checkAndCreateMaintenanceAlarm(device, maintenancePlanUpdated);
                                break;
                            case EVENT:
                                log.info("Processing device {} for maintenance plan {} with EVENT notification class", device.getId().getValue(), maintenancePlanUpdated.getName());
                                checkAndCreateMaintenanceEvent(device, maintenancePlanUpdated);
                                break;
                            default:
                                log.warn("Unknown notification class {} for maintenance plan {}", maintenancePlanUpdated.getNotificationClass(), maintenancePlanUpdated.getName());
                                continue;
                        }

                    }
                }
                log.info("time-based maintenance devices processed");
                return Boolean.TRUE;
            } catch (Exception e) {
                log.error("Maintenance job for tenant {} failed with message {}", context.getTenant(), e.getMessage(), e);
            }
            return Boolean.FALSE;
        });
        return callWithinContext;
    }

    private void checkAndCreateMaintenanceEvent(ManagedObjectRepresentation device,
            MaintenancePlan maintenancePlanUpdated) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkAndCreateMaintenanceEvent'");
    }

    private MaintenancePlan checkAndUpdateActivateFlag(MaintenancePlan maintenancePlan) {
        DateTime startDate = maintenancePlan.getStartDate();
        DateTime endDate = maintenancePlan.getEndDate();
        Boolean currentActivate = maintenancePlan.getActive();
        if(startDate == null && endDate == null) {
            return maintenancePlan;
        }

        DateTime currentDate = new DateTime();
        Boolean newActive = startDate != null && currentDate.isAfter(startDate) || endDate != null && currentDate.isBefore(endDate);
        if(currentActivate == null || !currentActivate.equals(newActive)) {
            log.info("Updating maintenance plan {} activate flag from {} to {}", maintenancePlan.getName(), currentActivate, newActive);
            return maintenancePlanService.updatActivateFlag(maintenancePlan.getId(), newActive);
        }

        return maintenancePlan;
    }

    private void checkAndCreateMaintenanceAlarm(ManagedObjectRepresentation device, MaintenancePlan maintenancePlan) {
        //load last maintenance timestamp
        DateTime lastMaintenance = null;
        
        try {
            Object lastMaintenanceObj = device.get(MaintenancePlanMapper.DEVICE_LAST_MAINTENANCE);
            if (lastMaintenanceObj instanceof String) {
                lastMaintenance = DateTime.parse((String) lastMaintenanceObj);
            }
        } catch (Exception e) {
            log.warn("Could not retrieve last maintenance time for device {}", device.getId().getValue());
        }

        //if not set load device creation time
        if(lastMaintenance == null) {
            log.info("No last maintenance timestamp found for device {}, using creation time", device.getId().getValue());
            lastMaintenance = device.getCreationDateTime();
        }
        
        //load cron expression from maintenance plan
        String cronExpr = maintenancePlan.getOnTime().getCronExpression();
        log.info("Calculating next maintenance time for device {} with last maintenance at {} and cron expression {}",
                device.getId().getValue(), lastMaintenance.toString(), cronExpr);
        
        // Parse cron expression and calculate next maintenance
        DateTime nextMaintenance = null;
        try {
            CronExpression cron = CronExpression.parse(cronExpr);
            // Convert Joda DateTime to Java Time LocalDateTime
            LocalDateTime lastMaintenanceLocal = LocalDateTime.ofInstant(
                lastMaintenance.toDate().toInstant(),
                ZoneId.systemDefault()
            );
            // Calculate next execution
            LocalDateTime nextLocal = cron.next(lastMaintenanceLocal);
            if (nextLocal != null) {
                // Convert back to Joda DateTime
                ZonedDateTime zonedNext = nextLocal.atZone(ZoneId.systemDefault());
                nextMaintenance = new DateTime(zonedNext.toInstant().toEpochMilli());
            }
        } catch (Exception e) {
            log.error("Invalid cron expression '{}' for maintenance plan {}", cronExpr, maintenancePlan.getName(), e);
            return;
        }
        
        if (nextMaintenance == null) {
            log.warn("Could not calculate next maintenance time for device {}", device.getId().getValue());
            return;
        }
        
        log.info("Next maintenance for device {} is scheduled at {}", device.getId().getValue(), nextMaintenance.toString());

        //maintenancePlan.getOnTime().getInterval();

        //check if maintenance is due and create alarm if needed
        DateTime currentTime = new DateTime();
        if(currentTime.isAfter(nextMaintenance) || currentTime.isEqual(nextMaintenance)) {
            log.info("Maintenance is DUE for device {}", device.getId().getValue());
            createAlarmNotification(device, maintenancePlan, nextMaintenance, lastMaintenance);
        } else {
            log.info("Maintenance is NOT yet due for device {}", device.getId().getValue());
        }

        //set next maintenance time in device fragment if it has changed
        DateTime orginNextMaintenance = null;
        
        try {
            Object orginNextMaintenanceObj = device.get(MaintenancePlanMapper.DEVICE_NEXT_MAINTENANCE);
            if (orginNextMaintenanceObj instanceof String) {
                orginNextMaintenance = DateTime.parse((String) orginNextMaintenanceObj);
            }
        } catch (Exception e) {
            log.warn("Could not retrieve last maintenance time for device {}", device.getId().getValue());
        }
        if(orginNextMaintenance == null || !orginNextMaintenance.isEqual(nextMaintenance)) {
            log.info("Updating next maintenance time for device {} to {}", device.getId().getValue(), nextMaintenance.toString());
            ManagedObjectRepresentation deviceUpdate = new ManagedObjectRepresentation();
            deviceUpdate.setId(device.getId());
            deviceUpdate.set(nextMaintenance, MaintenancePlanMapper.DEVICE_NEXT_MAINTENANCE);
            try {
                inventoryApi.update(deviceUpdate);
            } catch (Exception e) {
                log.error("Could not update next maintenance time for device {}", device.getId().getValue(), e);
            }
        }else {
            log.info("Next maintenance time for device {} is already up to date", device.getId().getValue());
        }
    }

    private AlarmRepresentation createAlarmNotification(ManagedObjectRepresentation device, MaintenancePlan maintenancePlan, DateTime nextMaintenance, DateTime lastMaintenance) {
        AlarmRepresentation alarm = new AlarmRepresentation();
        alarm.setSource(device);
        alarm.setType(maintenancePlan.getNotificationType() != null ? maintenancePlan.getNotificationType() : MaintenancePlanMapper.ALARM_TYPE);
        alarm.setStatus("ACTIVE");
        alarm.setSeverity(maintenancePlan.getNotificationSeverity() != null ? maintenancePlan.getNotificationSeverity().name() : "MAJOR");
        alarm.setText(maintenancePlan.getNotificationText() != null ? maintenancePlan.getNotificationText() : maintenancePlan.getName() + " is due for device ");
        alarm.setDateTime(new DateTime());
        alarm.set(nextMaintenance, MaintenancePlanMapper.DEVICE_NEXT_MAINTENANCE);
        alarm.set(lastMaintenance, MaintenancePlanMapper.DEVICE_LAST_MAINTENANCE);
        alarm.set(maintenancePlan.getId(), MaintenancePlanMapper.MAINTENANCE_PLAN_ID);
        return alarmApi.create(alarm);
    }

}
