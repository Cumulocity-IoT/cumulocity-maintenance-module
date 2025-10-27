package cumulocity.microservice.maintenancemodule.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a schema for updating a maintenance plan, excluding the ID which cannot be modified.
 * Provides functionality to update existing maintenance schedules for Cumulocity devices.
 * 
 * @author APES
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@Schema(description = "Schema for updating a maintenance plan, excluding the ID which cannot be modified")
@Validated
public class MaintenancePlanUpdate {

    @Schema(description = "The name of the maintenance plan", example = "Monthly Equipment Check")
    private String name;

    @Schema(description = "Detailed description of the maintenance plan", example = "Monthly maintenance check for all production equipment")
    private String description;

    @Schema(description = "Maintenance text which can be used to initialize the maintenance instance")
    private String text;

    @Schema(description = "Start date and time for the maintenance", example = "2025-01-01T09:00:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startDate;

    @Schema(description = "End date and time for the maintenance", example = "2025-12-31T17:00:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime endDate;

    @Schema(description = "Indicates whether the maintenance plan is active", example = "true")
    private Boolean active;

    @Schema(description = "List of maintenance triggers")
    @Valid
    private List<MaintenanceTrigger> on;
}
