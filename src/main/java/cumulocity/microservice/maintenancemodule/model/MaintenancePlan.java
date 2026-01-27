package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a complete maintenance plan with all fields including generated ID.
 * Provides functionality to manage maintenance schedules and operations for Cumulocity devices.
 * 
 * @author APES
 * @since 1.0.0
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents a complete maintenance plan with all fields including generated ID")
@Validated
public class MaintenancePlan {

    @Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "Internal ID, set by Cumulocity", example = "1234")
    @NotNull
    @NonNull
    private String id;

    @Schema(required = true, description = "The name of the maintenance plan", example = "Monthly Equipment Check")
    @NotNull
    @NonNull
    private String name;

    @Schema(description = "Detailed description of the maintenance plan", example = "Monthly maintenance check for all production equipment")
    private String description;

    @Schema(description = "Maintenance text which can be used to initialize the maintenance alarm text")
    private String notificationText;

    @Schema(description = "Maintenance type which can be used to initialize the maintenance alarm type")
    private String notificationType;

    @Schema(required = true, description = "Start date and time for the maintenance", example = "2025-01-01T09:00:00Z")
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
