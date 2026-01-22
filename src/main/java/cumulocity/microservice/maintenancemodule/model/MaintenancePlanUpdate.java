package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import jakarta.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

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

    @Schema(description = "Maintenance text which can be used to initialize the maintenance alarm text")
    private String notificationText;

    @Schema(description = "Maintenance type which can be used to initialize the maintenance alarm type")
    private String notificationType;

    @Schema(description = "Start date and time for the maintenance", example = "2025-01-01T09:00:00Z")
    private DateTime startDate;

    @Schema(description = "End date and time for the maintenance", example = "2025-12-31T17:00:00Z")
    private DateTime endDate;

    @Schema(description = "Indicates whether the maintenance plan is active", example = "true")
    private Boolean active;

    @Schema(description = "Device assignment filter criteria")
    @Valid
    private DeviceAssignmentCriteria apply;

    @Schema(description = "Time-based maintenance trigger definition")
    @Valid
    private TimeBasedTrigger onTime;

    @Schema(description = "Usage-based maintenance trigger definition")
    @Valid
    private UsageBasedTrigger onUsage;

    @Schema(description = "Condition-based maintenance trigger definitions. Conditions are combined using logical AND.")
    @Valid
    private List<ConditionBasedTrigger> onConditions;
}
