package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.QueryParam;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;

import cumulocity.microservice.maintenancemodule.model.DeviceAssignmentCriteria;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MaintenancePlanApplyService {

    private InventoryApi inventoryApi;

    private IdentityApi identityApi;

    public MaintenancePlanApplyService(InventoryApi inventoryApi, IdentityApi identityApi) {
        this.inventoryApi = inventoryApi;
        this.identityApi = identityApi;
    }

    /**
     * Get all devices to which the maintenance plan is applied, the apply criteria are processed with OR logic
     * 
     * @param mplan Maintenance plan
     * @return Set of devices
     */
    public Set<ManagedObjectRepresentation> getAllAppliedDevices(MaintenancePlan mplan) {
        List<ManagedObjectRepresentation> devices = new ArrayList<>();
        DeviceAssignmentCriteria applyCriteria = mplan.getApply();
        devices.addAll(getDevicesByIds(applyCriteria.getIdsInternal()));
        devices.addAll(getDevicesBySerial(applyCriteria.getIdsSerial()));
        devices.addAll(getDevicesByType(applyCriteria.getTypes()));
        devices.addAll(getDevicesByQuery(applyCriteria.getQuery()));
        return new HashSet<>(devices);
    }

    private List<ManagedObjectRepresentation> getDevicesByIds(List<String> deviceIds) {
        List<ManagedObjectRepresentation> devices = new ArrayList<>();
        for (String id : deviceIds) {
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
        List<ManagedObjectRepresentation> devices = new ArrayList<>();
        for (String serial : serialNumbers) {
            log.info("Getting device by serial number: {}", serial);
            try {
                ID identity = new ID("c8y_Serial", serial);
                ExternalIDRepresentation externalId = identityApi.getExternalId(identity);
                ManagedObjectRepresentation device = inventoryApi.get(externalId.getManagedObject().getId());
                devices.add(device);
            } catch (Exception e) {
                log.warn("Device with serial number {} not found", serial);
                continue;
            }
        }
        return devices;
    }

    private List<ManagedObjectRepresentation> getDevicesByType(List<String> deviceTypes) {
        List<ManagedObjectRepresentation> devices = new ArrayList<>();
        for (String type : deviceTypes) {
            log.info("Getting devices by type: {}", type);
            InventoryFilter filter = new InventoryFilter();
            filter.byType(type);
            inventoryApi.getManagedObjectsByFilter(filter).get(2000).allPages().forEach(devices::add);
        }
        return devices;
    }

    private List<ManagedObjectRepresentation> getDevicesByQuery(String query) {
        List<ManagedObjectRepresentation> devices = new ArrayList<>();
        log.info("Getting devices by query: {}", query);
        QueryParam queryParam = CustomQueryParam.QUERY.setValue(query).toQueryParam();
        inventoryApi.getManagedObjects().get(2000, queryParam).allPages().forEach(devices::add);
        return devices;
    }
}
