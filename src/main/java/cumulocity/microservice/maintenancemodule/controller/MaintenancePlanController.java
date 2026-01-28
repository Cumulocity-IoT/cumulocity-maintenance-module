package cumulocity.microservice.maintenancemodule.controller;

import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanListResponse;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanProposal;
import cumulocity.microservice.maintenancemodule.service.c8y.MaintenancePlanService;

@RestController
@RequestMapping("/api/maintenance-plans")
public class MaintenancePlanController {

    private final MaintenancePlanService maintenancePlanService;

    @Autowired
    public MaintenancePlanController(MaintenancePlanService maintenancePlanService) {
        this.maintenancePlanService = maintenancePlanService;
    }

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

        MaintenancePlanListResponse response = maintenancePlanService.getAllMaintenancePlans(active, startDate, endDate, pageSize, pageNumber, false);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlan> createMaintenancePlan(@RequestBody MaintenancePlanCreate maintenancePlanCreate) {
        MaintenancePlan createdPlan = maintenancePlanService.createMaintenancePlan(maintenancePlanCreate);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlan> getMaintenancePlan(@PathVariable Integer id) {
        MaintenancePlan maintenancePlan = maintenancePlanService.getMaintenancePlan(id);
        return new ResponseEntity<>(maintenancePlan, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenancePlan> updateMaintenancePlan(@PathVariable Integer id, @RequestBody MaintenancePlan maintenancePlan) {
        MaintenancePlan updatedPlan = maintenancePlanService.updateMaintenancePlan(id, maintenancePlan);
        return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteMaintenancePlan(@PathVariable Integer id) {
        maintenancePlanService.deleteMaintenancePlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}