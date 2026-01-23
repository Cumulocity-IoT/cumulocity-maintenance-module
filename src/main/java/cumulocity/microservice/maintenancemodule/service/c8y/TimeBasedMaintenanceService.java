package cumulocity.microservice.maintenancemodule.service.c8y;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.ManagedObject;

import cumulocity.microservice.maintenancemodule.model.DeviceAssignmentCriteria;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;

@Service
public class TimeBasedMaintenanceService {
    private static final Logger log = LoggerFactory.getLogger(TimeBasedMaintenanceService.class);

    private final ContextService<MicroserviceCredentials> contextService;

    private final MaintenancePlanService maintenancePlanService;

    private final InventoryApi inventoryApi;

    @Autowired
    public TimeBasedMaintenanceService(ContextService<MicroserviceCredentials> contextService, MaintenancePlanService maintenancePlanService, InventoryApi inventoryApi) {
        this.contextService = contextService;
        this.maintenancePlanService = maintenancePlanService;
        this.inventoryApi = inventoryApi;
    }

    public Boolean runJobWithinContext(MicroserviceCredentials context) {
        Boolean callWithinContext = contextService.callWithinContext(context, (Callable<Boolean>) () -> {
            try {
                log.info("Start time-based maintenance job for tenant {}", context.getTenant());
                List<MaintenancePlan> maintenancePlans = maintenancePlanService.getActiveTimeBasedMaintenancePlans();
                log.info("Found {} active time-based maintenance plans", maintenancePlans.size());
                for (MaintenancePlan maintenancePlan : maintenancePlans) {
                    Set<ManagedObjectRepresentation> devices = new HashSet<>();
                    DeviceAssignmentCriteria applyCriteria = maintenancePlan.getApply();
                    devices.addAll(getDevicesByIds(applyCriteria.getIdsInternal()));
                    devices.addAll(getDevicesBySerial(applyCriteria.getIdsSerial()));
                    devices.addAll(getDevicesByType(applyCriteria.getTypes()));
                    devices.addAll(getDevicesByQuery(applyCriteria.getQuery()));
                    log.info("Maintenance plan {} applies to {} devices", maintenancePlan.getName(), devices.size());
                    for (ManagedObjectRepresentation device : devices) {
                        checkAndCreateMaintenanceAlarm(device, maintenancePlan);
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

    private List<ManagedObjectRepresentation> getDevicesByIds(List<String> deviceIds) {
        List<ManagedObjectRepresentation> devices = new ArrayList<>();
        for(String id : deviceIds) {
            log.info("Getting device by ID: {}", id);
            try {
                devices.add(inventoryApi.get(GId.asGId(id)));
            } catch (Exception e) {
                log.warn("Device with ID {} not found", id);
                continue;
            }

        }
        return devices;
    }

    private List<ManagedObjectRepresentation> getDevicesBySerial(List<String> serialNumbers) {       
        return List.of();
    }

    private List<ManagedObjectRepresentation> getDevicesByType(List<String> deviceTypes) {
        return List.of();
    }

    private List<ManagedObjectRepresentation> getDevicesByQuery(String query) {
        return List.of();
    }

    private void checkAndCreateMaintenanceAlarm(ManagedObjectRepresentation device, MaintenancePlan maintenancePlan) {
        //TODO load last maintenance timestamp
        //device.get("mm_LastMaintenance");
        //TODO if not set load device creation time
        //device.getCreationTime();
        //TODO load inteval from maintenance plan
        //maintenancePlan.getOnTime().getInterval();
        //TODO calculate next maintenance time with all this data
        //TODO chekck if maintenance is due
        //TODO create maintenance alarm if due
        //TODO set next maintenance time in device fragment if it has changed
    }

}
