package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a schema for creating a new maintenance plan, excluding the auto-generated ID.
 * Provides functionality to create maintenance schedules for Cumulocity devices.
 * * @author APES
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Schema for creating a new maintenance plan, excluding the auto-generated ID")
@Validated
public class MaintenancePlanCreate {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "The name of the maintenance plan", example = "Monthly Equipment Check")
    @NotNull
    private String name;

    @Schema(description = "Detailed description of the maintenance plan", example = "Monthly maintenance check for all production equipment")
    private String description;

    @Schema(description = "Maintenance text which can be used to initialize the maintenance alarm text")
    private String notificationText;

    @Schema(description = "Maintenance type which can be used to initialize the maintenance alarm type")
    private String notificationType;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Start date and time for the maintenance", example = "2025-01-01T09:00:00Z")
    @NotNull
    private DateTime startDate;

    @Schema(description = "End date and time for the maintenance", example = "2025-12-31T17:00:00Z")
    @NotNull
    private DateTime endDate;

    @Schema(description = "Indicates whether the maintenance plan is active", example = "true")
    private Boolean active;

    @Schema(description = "List of triggers (combined view)")
    @Valid
    private List<MaintenanceTrigger> on; // This fixes the Mapper error!

    @Schema(description = "Device assignment filter criteria")
    @Valid
    private DeviceAssignmentCriteria apply;

    // --- AI Fields ---

    @Schema(description = "Frequency of the maintenance")
    private String frequency;

    @Schema(description = "ID of the equipment")
    private String equipmentId;

    @Schema(description = "List of required skills")
    private List<String> requiredSkills;

    @Schema(description = "List of specific maintenance tasks")
    private List<Object> tasks;

    // --- Specific Trigger Fields ---

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