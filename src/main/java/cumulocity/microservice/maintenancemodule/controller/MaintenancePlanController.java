package cumulocity.microservice.maintenancemodule.controller;

import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanListResponse;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanProposal;
import cumulocity.microservice.maintenancemodule.service.c8y.MaintenancePlanService;
import cumulocity.microservice.maintenancemodule.service.c8y.MaintenancePlanService;

/**
 * REST Controller for managing maintenance plans.
 * Provides CRUD operations for maintenance plans with time-based, usage-based,
 * and condition-based maintenance triggers.
 * * @author APES
 */
@RestController
@RequestMapping("/api/maintenance-plans")
public class MaintenancePlanController {

    private final MaintenancePlanService maintenancePlanService;

    @Autowired
    public MaintenancePlanController(MaintenancePlanService maintenancePlanService) {
        this.maintenancePlanService = maintenancePlanService;
    }

    /**
     * Get all maintenance plans with optional filtering and pagination
     *
     * @param active Filter by active status (optional)
     * @param startDate Filter plans starting after this date (optional)
     * @param endDate Filter plans ending before this date (optional)
     * @param pageSize Maximum number of items to return (default: 20, max: 100)
     * @param pageNumber Number of items to skip (default: 0)
     * @return List of maintenance plans with pagination information
     */
    @PostMapping(path = "/ai", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> proposeMaintenancePlan(@RequestBody Map<String, String> body) {
        String prompt = body.get("userprompt");
        if (prompt == null || prompt.isBlank()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        try {
            MaintenancePlanProposal proposal = maintenancePlanService.proposeMaintenancePlan(prompt);
            return new ResponseEntity<>(proposal, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlanListResponse> getAllMaintenancePlans(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "0") Integer pageNumber) {

        // Validate limit parameter
        if (pageSize < 1) {
            pageSize = 1;
        }

        // Validate offset parameter
        if (pageNumber < 0) {
            pageNumber = 0;
        }

        MaintenancePlanListResponse response = maintenancePlanService.getAllMaintenancePlans(active, startDate, endDate, pageSize, pageNumber, false);
        MaintenancePlanListResponse response = maintenancePlanService.getAllMaintenancePlans(
                active, startDate, endDate, pageSize, pageNumber, false);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Create a new maintenance plan
     *
     * @param maintenancePlanCreate The maintenance plan to create
     * @return The created maintenance plan with generated ID
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlan> createMaintenancePlan(@RequestBody MaintenancePlanCreate maintenancePlanCreate) {
    public ResponseEntity<MaintenancePlan> createMaintenancePlan(
            @RequestBody MaintenancePlanCreate maintenancePlanCreate) {

        MaintenancePlan createdPlan = maintenancePlanService.createMaintenancePlan(maintenancePlanCreate);

        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    /**
     * Get a specific maintenance plan by ID
     *
     * @param id The unique identifier of the maintenance plan
     * @return The maintenance plan if found
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlan> getMaintenancePlan(@PathVariable Integer id) {
    public ResponseEntity<MaintenancePlan> getMaintenancePlan(@PathVariable String id) {

        MaintenancePlan maintenancePlan = maintenancePlanService.getMaintenancePlan(id);

        if (maintenancePlan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(maintenancePlan, HttpStatus.OK);
    }

    /**
     * Update an existing maintenance plan by replacing all fields
     *
     * @param id The unique identifier of the maintenance plan
     * @param maintenancePlan The updated maintenance plan data
     * @return The updated maintenance plan
     */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlan> updateMaintenancePlan(@PathVariable Integer id, @RequestBody MaintenancePlan maintenancePlan) {
    public ResponseEntity<MaintenancePlan> updateMaintenancePlan(
            @PathVariable String id,
            @RequestBody MaintenancePlan maintenancePlan) {

        MaintenancePlan updatedPlan = maintenancePlanService.updateMaintenancePlan(id, maintenancePlan);

        if (updatedPlan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
    }

    /**
     * Delete an existing maintenance plan by ID
     *
     * @param id The unique identifier of the maintenance plan
     * @return Empty response with 204 status
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteMaintenancePlan(@PathVariable Integer id) {
    public ResponseEntity<Void> deleteMaintenancePlan(@PathVariable String id) {

        maintenancePlanService.deleteMaintenancePlan(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}