package cumulocity.microservice.maintenancemodule.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.PageStatisticsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;
import com.cumulocity.sdk.client.inventory.PagedManagedObjectCollectionRepresentation;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.client.QueryParam;
import com.cumulocity.sdk.client.SDKException;

import cumulocity.microservice.maintenancemodule.model.DeviceAssignmentCriteria;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanListResponse;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanType;
import cumulocity.microservice.maintenancemodule.service.c8y.CustomQueryParam;
import cumulocity.microservice.maintenancemodule.service.c8y.MaintenancePlanMapper;

/**
 * Service for managing maintenance plans with CRUD operations.
 * Provides functionality to create, retrieve, update, and delete maintenance plans.
 * 
 * @author APES
 * @since 1.0.0
 */
@Service
public class MaintenancePlanService {

    private static final Logger log = LoggerFactory.getLogger(MaintenancePlanService.class);
    
    private final InventoryApi inventoryApi;

    public MaintenancePlanService(InventoryApi inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    public MaintenancePlanListResponse getActiveMaintenancePlansByType(MaintenancePlanType maintenancePlanType, Integer pageSize, Integer pageNumber) {
        //TODO check if we need also to check start and end date with current date, DateTime now = new DateTime();
        QueryParam maintenancePlanTypeQuery = null;
        if(maintenancePlanType == null) {
            maintenancePlanTypeQuery = CustomQueryParam.QUERY.setValue(MaintenancePlanMapper.MP_ACTIVE + " eq true").toQueryParam();
        }else {
            maintenancePlanTypeQuery = CustomQueryParam.QUERY.setValue("has("+ MaintenancePlanMapper.MP_ON_TIME + ") and " + MaintenancePlanMapper.MP_ACTIVE + " eq true").toQueryParam();
        }
        
        PagedManagedObjectCollectionRepresentation managedObjects = inventoryApi.getManagedObjects().get(pageSize, maintenancePlanTypeQuery);
         
        List<MaintenancePlan> allMaintenancePlans = new ArrayList<>();
        for (ManagedObjectRepresentation managedObject : managedObjects.getManagedObjects()) {
            MaintenancePlan maintenancePlan = MaintenancePlanMapper.map2(managedObject);
            if (maintenancePlan != null) {
                allMaintenancePlans.add(maintenancePlan);
            }
        }
        PageStatisticsRepresentation pageStatistics = managedObjects.getPageStatistics();


        MaintenancePlanListResponse response = new MaintenancePlanListResponse();
        response.setMaintenancePlans(allMaintenancePlans);
        response.setCurrentPage(pageStatistics.getCurrentPage());
        response.setPageSize(pageStatistics.getPageSize());
        response.setTotalPages(pageStatistics.getTotalPages());
        response.setTotalElements(pageStatistics.getTotalElements());

        return response;
    }

    /**
     * Creates a new maintenance plan in the Cumulocity IoT Platform.
     * 
     * @param maintenancePlanCreate The maintenance plan configuration to create
     * @return The created maintenance plan with assigned ID
     * @throws IllegalArgumentException if maintenancePlanCreate is null or invalid
     * @throws RuntimeException if creation fails in Cumulocity
     * @since 1.0.0
     */
    public MaintenancePlan createMaintenancePlan(MaintenancePlanCreate maintenancePlanCreate) {
        // Validate input data
        if (maintenancePlanCreate == null) {
            throw new IllegalArgumentException("MaintenancePlanCreate cannot be null");
        }
        validateMaintenancePlanCreate(maintenancePlanCreate);
        
        // Create mapper and convert to ManagedObjectRepresentation
        MaintenancePlanMapper mapper = MaintenancePlanMapper.map2(maintenancePlanCreate);
        ManagedObjectRepresentation managedObject = mapper.getManagedObject();
        
        try {
            // Store in Cumulocity inventory
            ManagedObjectRepresentation createdObject = inventoryApi.create(managedObject);
            
            // Convert back to MaintenancePlan using mapper
            return MaintenancePlanMapper.map2(createdObject);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create maintenance plan in Cumulocity: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all maintenance plans with optional filtering and pagination.
     * 
     * @param active filter by active status (optional)
     * @param startDate filter plans starting after this date (optional)
     * @param endDate filter plans ending before this date (optional)
     * @param pageSize maximum number of items per page
     * @param pageNumber page number (0-based)
     * @param withTotalPages whether to include total page statistics in response
     * @return list response with pagination information
     * @since 1.0.0
     */
    public MaintenancePlanListResponse getAllMaintenancePlans(Boolean active, DateTime startDate, 
                                                              DateTime endDate, Integer pageSize, 
                                                              Integer pageNumber, Boolean withTotalPages) {
        log.info("getAllMaintenancePlans(active: {}, startDate: {}, endDate: {}, pageSize: {}, pageNumber: {}, withTotalPages: {})", 
                 active, startDate, endDate, pageSize, pageNumber, withTotalPages);
        
        // Set default values
        pageSize = pageSize != null ? pageSize : 5;
        pageNumber = pageNumber != null ? pageNumber : 0;
        withTotalPages = withTotalPages != null ? withTotalPages : false;
        
        try {
            // Create filter for maintenance plans
            InventoryFilter filter = new InventoryFilter();
            filter.byType(MaintenancePlanMapper.MANAGED_OBJECT_TYPE);
            
            // Get managed objects from Cumulocity
            ManagedObjectCollection managedObjectCollection = inventoryApi.getManagedObjectsByFilter(filter);
            
            // Get all maintenance plans and convert to domain objects
            Iterable<ManagedObjectRepresentation> allPages = managedObjectCollection.get(2000).allPages();
            List<MaintenancePlan> allMaintenancePlans = new ArrayList<>();
            
            for (ManagedObjectRepresentation managedObject : allPages) {
                MaintenancePlan maintenancePlan = MaintenancePlanMapper.map2(managedObject);
                if (maintenancePlan != null) {
                    allMaintenancePlans.add(maintenancePlan);
                }
            }
            
            // Apply filters
            List<MaintenancePlan> filteredPlans = allMaintenancePlans.stream()
                .filter(plan -> filterByActive(plan, active))
                .filter(plan -> filterByDateRange(plan, startDate, endDate))
                .collect(Collectors.toList());
            
            // Apply pagination
            int totalElements = filteredPlans.size();
            int totalPages = (int) Math.ceil((double) totalElements / pageSize);
            int startIndex = pageNumber * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalElements);
            
            List<MaintenancePlan> pagedPlans = new ArrayList<>();
            if (startIndex < totalElements) {
                pagedPlans = filteredPlans.subList(startIndex, endIndex);
            }
            
            // Create response
            MaintenancePlanListResponse response = new MaintenancePlanListResponse();
            response.setMaintenancePlans(pagedPlans);
            response.setCurrentPage(pageNumber);
            response.setPageSize(pageSize);
            response.setTotalElements((long) totalElements);
            
            if (withTotalPages) {
                response.setTotalPages(totalPages);
            }
            
            log.info("getAllMaintenancePlans: returning {} plans out of {} total", pagedPlans.size(), totalElements);
            return response;
            
        } catch (SDKException e) {
            log.error("Failed to retrieve maintenance plans: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve maintenance plans: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving maintenance plans: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error while retrieving maintenance plans: " + e.getMessage(), e);
        }
    }
    
    /**
     * Filters maintenance plans by active status.
     * 
     * @param plan the maintenance plan to check
     * @param active the active status filter (null means no filter)
     * @return true if plan matches filter criteria
     * @since 1.0.0
     */
    private boolean filterByActive(MaintenancePlan plan, Boolean active) {
        if (active == null) {
            return true; // No filter applied
        }
        return active.equals(plan.getActive());
    }
    
    /**
     * Filters maintenance plans by date range.
     * 
     * @param plan the maintenance plan to check
     * @param startDate the start date filter (null means no filter)
     * @param endDate the end date filter (null means no filter)
     * @return true if plan matches filter criteria
     * @since 1.0.0
     */
    private boolean filterByDateRange(MaintenancePlan plan, DateTime startDate, DateTime endDate) {
        if (startDate != null && plan.getStartDate() != null) {
            if (plan.getStartDate().isBefore(startDate)) {
                return false;
            }
        }
        
        if (endDate != null && plan.getEndDate() != null) {
            if (plan.getEndDate().isAfter(endDate)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validate maintenance plan create data
     * 
     * @param maintenancePlanCreate The maintenance plan to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateMaintenancePlanCreate(MaintenancePlanCreate maintenancePlanCreate) {
        if (maintenancePlanCreate.getName() == null || maintenancePlanCreate.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Maintenance plan name is required");
        }
        
        if (maintenancePlanCreate.getStartDate() != null && maintenancePlanCreate.getEndDate() != null) {
            if (maintenancePlanCreate.getStartDate().isAfter(maintenancePlanCreate.getEndDate())) {
                throw new IllegalArgumentException("Start date must be before end date");
            }
        }
        
        // Additional validation for triggers, at least one trigger must be defined
        boolean atLeastOneTriggerDefined = (maintenancePlanCreate.getOnTime() != null || maintenancePlanCreate.getOnUsage() != null || !maintenancePlanCreate.getOnConditions().isEmpty());
        if (!atLeastOneTriggerDefined) {
            throw new IllegalArgumentException("Maintenance plan validation failed: at least one trigger must be defined");
        }

    }

    /**
     * Get a specific maintenance plan by ID
     * 
     * @param id The unique identifier of the maintenance plan
     * @return The maintenance plan if found
     * @throws IllegalArgumentException if id is null
     * @throws RuntimeException if plan not found or retrieval fails
     * @since 1.0.0
     */
    public MaintenancePlan getMaintenancePlan(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Maintenance plan ID cannot be null");
        }
        
        log.debug("Retrieving maintenance plan with ID: {}", id);
        
        try {
            ManagedObjectRepresentation managedObject = inventoryApi.get(GId.asGId(id));
            
            if (managedObject == null) {
                log.warn("Maintenance plan with ID {} not found", id);
                return null;
            }
            
            // Convert ManagedObjectRepresentation back to MaintenancePlan using mapper
            MaintenancePlan maintenancePlan = MaintenancePlanMapper.map2(managedObject);
            
            log.debug("Successfully retrieved maintenance plan with ID: {}", id);
            return maintenancePlan;
            
        } catch (SDKException e) {
            log.error("Failed to retrieve maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve maintenance plan with ID " + id + ": " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Unexpected error while retrieving maintenance plan: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing maintenance plan by replacing all fields.
     * 
     * @param id the unique identifier of the maintenance plan to update
     * @param maintenancePlan the updated maintenance plan data
     * @return the updated maintenance plan
     * @throws IllegalArgumentException if id or maintenancePlan is null or invalid
     * @throws RuntimeException if plan not found or update fails
     * @since 1.0.0
     */
    public MaintenancePlan updateMaintenancePlan(String id, MaintenancePlan maintenancePlan) {
        if(validateMaintenancePlan(maintenancePlan) == false) {
            return null; // Validation failed, return null
        }

        log.info("updateMaintenancePlan(id: {}, maintenancePlan: {})", id, maintenancePlan.toString());
               
        try {
            
            // Validate that the ID in the maintenance plan matches the provided ID
            maintenancePlan.setId(id); // Ensure ID is set for update

            // Create mapper and convert to ManagedObjectRepresentation
            MaintenancePlanMapper mapper = MaintenancePlanMapper.map2(maintenancePlan);
            ManagedObjectRepresentation managedObject = mapper.getManagedObject();
        
            // Update in Cumulocity inventory
            ManagedObjectRepresentation updatedObject = inventoryApi.update(managedObject);
            
            // Convert back to MaintenancePlan using mapper
            MaintenancePlan updatedPlan = MaintenancePlanMapper.map2(updatedObject);
            
            log.info("Successfully updated maintenance plan with ID: {}", id);
            return updatedPlan;
            
        } catch (SDKException e) {
            log.error("Failed to update maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Failed to update maintenance plan with ID " + id + ": " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while updating maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Unexpected error while updating maintenance plan: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an existing maintenance plan from the Cumulocity IoT Platform.
     * 
     * @param id the unique identifier of the maintenance plan to delete
     * @throws IllegalArgumentException if id is null
     * @throws RuntimeException if plan not found or deletion fails
     * @since 1.0.0
     */
    public void deleteMaintenancePlan(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Maintenance plan ID cannot be null");
        }
        
        log.debug("Deleting maintenance plan with ID: {}", id);
        
        try {
            // Delete the managed object from Cumulocity inventory
            inventoryApi.delete(GId.asGId(id.toString()));
            
            log.info("Successfully deleted maintenance plan with ID: {}", id);
            
        } catch (SDKException e) {
            log.error("Failed to delete maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Failed to delete maintenance plan with ID " + id + ": " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Unexpected error while deleting maintenance plan: " + e.getMessage(), e);
        }
    }

    /**
     * Validates maintenance plan data for updates.
     * 
     * @param maintenancePlan the maintenance plan to validate
     * @return true if validation passes, false otherwise
     * @since 1.0.0
     */
    private boolean validateMaintenancePlan(MaintenancePlan maintenancePlan) {

        if (maintenancePlan == null) {
            log.warn("Maintenance plan validation failed: maintenancePlan is null");
            return false;
        }

        if (maintenancePlan.getName() == null || maintenancePlan.getName().trim().isEmpty()) {
            log.warn("Maintenance plan validation failed: name is required");
            return false;
        }
        
        if (maintenancePlan.getStartDate() != null && maintenancePlan.getEndDate() != null) {
            if (maintenancePlan.getStartDate().isAfter(maintenancePlan.getEndDate())) {
                log.warn("Maintenance plan validation failed: start date must be before end date");
                return false;
            }
        }
        
        // Additional validation for triggers, at least one trigger must be defined
        boolean atLeastOneTriggerDefined = (maintenancePlan.getOnTime() != null || maintenancePlan.getOnUsage() != null || !maintenancePlan.getOnConditions().isEmpty());
        if (!atLeastOneTriggerDefined) {
            log.warn("Maintenance plan validation failed: at least one trigger must be defined");
            return false;
        }

        return true;
    }

    /**
     * Updates device assignment for a maintenance plan.
     * 
     * @param id the unique identifier of the maintenance plan
     * @param deviceAssignment the device assignment filter criteria
     * @return the updated device assignment
     * @throws IllegalArgumentException if id or deviceAssignment is null
     * @throws RuntimeException if plan not found or update fails
     * @since 1.0.0
     */
    public DeviceAssignmentCriteria updateDeviceAssignment(String id, DeviceAssignmentCriteria deviceAssignment) {
        if (id == null) {
            throw new IllegalArgumentException("Maintenance plan ID cannot be null");
        }
        if (deviceAssignment == null) {
            throw new IllegalArgumentException("DeviceAssignment cannot be null");
        }
        
        log.info("updateDeviceAssignment(id: {}, deviceAssignment: {})", id, deviceAssignment);
        
        try {
            // Get the existing maintenance plan
            MaintenancePlan maintenancePlan = getMaintenancePlan(id);
            if (maintenancePlan == null) {
                throw new RuntimeException("Maintenance plan with ID " + id + " not found");
            }
            
            // Update the apply field
            maintenancePlan.setApply(deviceAssignment);
            
            // Update the maintenance plan
            updateMaintenancePlan(id, maintenancePlan);
            
            log.info("Successfully updated device assignment for maintenance plan with ID: {}", id);
            return deviceAssignment;
            
        } catch (Exception e) {
            log.error("Failed to update device assignment for maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Failed to update device assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves device assignment for a maintenance plan.
     * 
     * @param id the unique identifier of the maintenance plan
     * @return the device assignment filter criteria
     * @throws IllegalArgumentException if id is null
     * @throws RuntimeException if plan not found or retrieval fails
     * @since 1.0.0
     */
    public DeviceAssignmentCriteria getDeviceAssignment(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Maintenance plan ID cannot be null");
        }
        
        log.debug("Retrieving device assignment for maintenance plan with ID: {}", id);
        
        try {
            MaintenancePlan maintenancePlan = getMaintenancePlan(id);
            if (maintenancePlan == null) {
                throw new RuntimeException("Maintenance plan with ID " + id + " not found");
            }
            
            DeviceAssignmentCriteria deviceAssignment = maintenancePlan.getApply();
            
            log.debug("Successfully retrieved device assignment for maintenance plan with ID: {}", id);
            return deviceAssignment;
            
        } catch (Exception e) {
            log.error("Failed to retrieve device assignment for maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve device assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Removes device assignment from a maintenance plan.
     * 
     * @param id the unique identifier of the maintenance plan
     * @throws IllegalArgumentException if id is null
     * @throws RuntimeException if plan not found or update fails
     * @since 1.0.0
     */
    public void deleteDeviceAssignment(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Maintenance plan ID cannot be null");
        }
        
        log.debug("Deleting device assignment for maintenance plan with ID: {}", id);
        
        try {
            // Get the existing maintenance plan
            MaintenancePlan maintenancePlan = getMaintenancePlan(id);
            if (maintenancePlan == null) {
                throw new RuntimeException("Maintenance plan with ID " + id + " not found");
            }
            
            // Remove the apply field
            maintenancePlan.setApply(null);
            
            // Update the maintenance plan
            updateMaintenancePlan(id, maintenancePlan);
            
            log.info("Successfully deleted device assignment for maintenance plan with ID: {}", id);
            
        } catch (Exception e) {
            log.error("Failed to delete device assignment for maintenance plan with ID: {}", id, e);
            throw new RuntimeException("Failed to delete device assignment: " + e.getMessage(), e);
        }
    }

}
