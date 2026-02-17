package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Schema(description = "Represents a complete maintenance plan with all fields including generated ID")
@Validated
public class MaintenancePlan {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    public enum MaintenanceNotificationClass {
        ALARM,
        EVENT
    }

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    public enum MaintenanceNotificationSeverity {
        CRITICAL,
        MAJOR,
        MINOR,
        WARNING
    }

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY, description = "Internal ID, set by Cumulocity", example = "1234")
    @NotNull
    @NonNull
    private String id;

    @Schema(required = true, description = "The name of the maintenance plan", example = "Monthly Equipment Check")
    @NotNull
    @NonNull
    private String name;

    @Schema(description = "Detailed description of the maintenance plan", example = "Monthly maintenance check for all production equipment")
    private String description;

    @Schema(description = "Maintenance notification class which initializes the maintenance notification class", example = "ALARM")
    @NotNull
    @NonNull
    private MaintenanceNotificationClass notificationClass;

    @Schema(description = "Maintenance text which can be used to initialize the maintenance alarm text")
    private String notificationText;

    @Schema(description = "Maintenance type which can be used to initialize the maintenance notification type")
    @NotNull
    @NonNull
    private String notificationType;

    @Schema(description = "Maintenance severity which can be used to initialize the maintenance notification severity", example = "CRITICAL")
    private MaintenanceNotificationSeverity notificationSeverity;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Start date", example = "2025-01-01T09:00:00Z")
    @NotNull
    private DateTime startDate;

    @Schema(description = "End date and time for the maintenance", example = "2025-12-31T17:00:00Z")
    @NotNull
    private DateTime endDate;

    @Schema(description = "Indicates whether the maintenance plan is active", example = "true")
    private Boolean active;

    @Schema(description = "List of triggers (combined view)")
    @Valid
    private List<MaintenanceTrigger> on;

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

    // --- Specific Trigger Fields (Optional/View specific) ---

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
