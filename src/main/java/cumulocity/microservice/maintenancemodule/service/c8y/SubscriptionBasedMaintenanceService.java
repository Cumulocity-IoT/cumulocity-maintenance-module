package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubscriptionBasedMaintenanceService {
    private final ContextService<MicroserviceCredentials> contextService;

    private final MaintenancePlanService maintenancePlanService;

    private final MaintenancePlanApplyService maintenancePlanApplyService;

    private final InventoryApi inventoryApi;

    private final AlarmApi alarmApi;

    public SubscriptionBasedMaintenanceService(ContextService<MicroserviceCredentials> contextService, MaintenancePlanService maintenancePlanService, MaintenancePlanApplyService maintenancePlanApplyService, InventoryApi inventoryApi, AlarmApi alarmApi) {
        this.contextService = contextService;
        this.maintenancePlanService = maintenancePlanService;
        this.maintenancePlanApplyService = maintenancePlanApplyService;
        this.inventoryApi = inventoryApi;
        this.alarmApi = alarmApi;
    }

    public Boolean runJobWithinContext(MicroserviceCredentials context) {
        Boolean callWithinContext = contextService.callWithinContext(context, (Callable<Boolean>) () -> {
            try {
                log.info("Start usage-based maintenance job for tenant {}", context.getTenant());
                List<MaintenancePlan> maintenancePlans = maintenancePlanService.getAllMaintenancePlansByType(MaintenancePlanMapper.MP_ON_USAGE);
                log.info("Found {} active usage-based maintenance plans", maintenancePlans.size());
                for (MaintenancePlan maintenancePlan : maintenancePlans) {
                    MaintenancePlan maintenancePlanUpdated = checkAndUpdateActivateFlag(maintenancePlan);
                    if(maintenancePlanUpdated.getActive() == null || !maintenancePlanUpdated.getActive()) {
                        log.info("Skipping inactive maintenance plan {}", maintenancePlan.getName());
                        continue;
                    }
                    Set<ManagedObjectRepresentation> devices = maintenancePlanApplyService.getAllAppliedDevices(maintenancePlanUpdated);
                    log.info("Maintenance plan {} applies to {} devices", maintenancePlanUpdated.getName(), devices.size());
                    //TODO fetch list of subscribed devices and compare with applied devices, if there is a difference
                    //if (exists in applied but not in subscribed) -> subscribe and add to subscribed devices list
                    //if (exists in subscribed but not in applied) -> unsubscribe and remove from subscribed devices list
                }
                log.info("usage-based maintenance devices processed");
                return Boolean.TRUE;
            } catch (Exception e) {
                log.error("Maintenance job for tenant {} failed with message {}", context.getTenant(), e.getMessage(), e);
            }
            return Boolean.FALSE;
        });
        return callWithinContext;
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
}
