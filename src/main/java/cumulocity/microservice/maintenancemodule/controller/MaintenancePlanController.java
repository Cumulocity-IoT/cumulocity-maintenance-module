package cumulocity.microservice.maintenancemodule.controller;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanListResponse;
import cumulocity.microservice.maintenancemodule.service.MaintenancePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST Controller for managing maintenance plans.
 * Provides CRUD operations for maintenance plans with time-based, usage-based, 
 * and condition-based maintenance triggers.
 * 
 * @author APES
 */
@RestController
@RequestMapping("/api/maintenance-plans")
public class MaintenancePlanController {

    private final MaintenancePlanService maintenancePlanService;

    public MaintenancePlanController(MaintenancePlanService maintenancePlanService) {
        this.maintenancePlanService = maintenancePlanService;
    }

    @Operation(summary = "Get all maintenance plans with optional filtering and pagination", description = "Returns a list of all maintenance plans in IoT Platform. Additional query parameters allow to filter that list. The default configuration will return all active maintenance plans!", tags = {}, parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "active", description = "Filter by active status", schema = @Schema(type = "boolean")),
            @Parameter(in = ParameterIn.QUERY, name = "startDate", description = "Filter plans starting after this date (ISO 8601 format)", schema = @Schema(type = "string", format = "date-time")),
            @Parameter(in = ParameterIn.QUERY, name = "endDate", description = "Filter plans ending before this date (ISO 8601 format)", schema = @Schema(type = "string", format = "date-time")),
            @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Maximum number of items to return (default: 20, max: 100)", schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(in = ParameterIn.QUERY, name = "pageNumber", description = "Number of items to skip (default: 0)", schema = @Schema(type = "integer", defaultValue = "0")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlanListResponse> getAllMaintenancePlans(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "0") Integer pageNumber) {
        
        // Validate limit parameter
        if (pageSize < 1) {
            pageSize = 1; // Reset to default if invalid
        }
        
        // Validate offset parameter
        if (pageNumber < 0) {
            pageNumber = 0; // Reset to default if invalid
        }

        MaintenancePlanListResponse response = maintenancePlanService.getAllMaintenancePlans(
                active, startDate, endDate, pageSize, pageNumber, false);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Create a new maintenance plan", description = "Creates a new maintenance plan in IoT Platform", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request") })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlan> createMaintenancePlan(
            @RequestBody MaintenancePlanCreate maintenancePlanCreate) {
        
        MaintenancePlan createdPlan = maintenancePlanService.createMaintenancePlan(maintenancePlanCreate);
        
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @Operation(summary = "GET maintenance plan by Id", description = "Returns maintenance plan by internal Id", parameters = {
            @Parameter(in = ParameterIn.PATH, name = "id", required = true, description = "Internal maintenance plan Id", schema = @Schema(type = "string")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlan> getMaintenancePlan(@PathVariable String id) {
        
        MaintenancePlan maintenancePlan = maintenancePlanService.getMaintenancePlan(id);
        
        return new ResponseEntity<>(maintenancePlan, HttpStatus.OK);
    }

    /**
     * Update an existing maintenance plan by replacing all fields
     * 
     * @param id The unique identifier of the maintenance plan
     * @param maintenancePlan The updated maintenance plan data
     * @return The updated maintenance plan
     */
    // @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<MaintenancePlan> updateMaintenancePlan(
    //         @PathVariable String id,
    //         @RequestBody MaintenancePlan maintenancePlan) {
        
    //     MaintenancePlan updatedPlan = maintenancePlanService.updateMaintenancePlan(id, maintenancePlan);
        
    //     return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
    // }

    /**
     * Delete an existing maintenance plan by ID
     * 
     * @param id The unique identifier of the maintenance plan
     * @return Empty response with 204 status
     */
    // @DeleteMapping(path = "/{id}")
    // public ResponseEntity<Void> deleteMaintenancePlan(@PathVariable String id) {
        
    //     maintenancePlanService.deleteMaintenancePlan(id);
        
    //     return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    // }

    /**
     * Update device assignment for a maintenance plan
     * 
     * @param id The unique identifier of the maintenance plan
     * @param deviceAssignment The device assignment filter criteria
     * @return The updated device assignment
     */
    // @PutMapping(path = "/{id}/apply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<DeviceAssignmentCriteria> updateDeviceAssignment(
    //         @PathVariable String id,
    //         @RequestBody DeviceAssignmentCriteria deviceAssignment) {
        
    //     DeviceAssignmentCriteria updated = maintenancePlanService.updateDeviceAssignment(id, deviceAssignment);
        
    //     return new ResponseEntity<>(updated, HttpStatus.OK);
    // }

    /**
     * Get device assignment for a maintenance plan
     * 
     * @param id The unique identifier of the maintenance plan
     * @return The device assignment filter criteria
     */
    // @GetMapping(path = "/{id}/apply", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<DeviceAssignmentCriteria> getDeviceAssignment(@PathVariable String id) {
        
    //     DeviceAssignmentCriteria deviceAssignment = maintenancePlanService.getDeviceAssignment(id);
        
    //     return new ResponseEntity<>(deviceAssignment, HttpStatus.OK);
    // }

    /**
     * Remove device assignment from a maintenance plan
     * 
     * @param id The unique identifier of the maintenance plan
     * @return Empty response with 204 status
     */
    // @DeleteMapping(path = "/{id}/apply")
    // public ResponseEntity<Void> deleteDeviceAssignment(@PathVariable String id) {
        
    //     maintenancePlanService.deleteDeviceAssignment(id);
        
    //     return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    // }
}
