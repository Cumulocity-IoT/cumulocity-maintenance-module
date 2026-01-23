package cumulocity.microservice.maintenancemodule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.maintenancemodule.model.MaintenanceAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST Controller for managing maintenance actions.
 * 
 * @author APES
 */
@RestController
@RequestMapping("/api/maintenance/actions")
public class MaintenanceActionController {
    
    public MaintenanceActionController() {
    }

    @Operation(summary = "Create a new maintenance action", description = "Creates a new maintenance action in IoT Platform", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request") })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MaintenanceAction> createMaintenanceAction(
            @RequestBody MaintenanceAction maintenanceAction) {
        

        return new ResponseEntity<>(maintenanceAction, HttpStatus.CREATED);
    }
}
