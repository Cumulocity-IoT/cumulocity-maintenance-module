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

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents a complete maintenance plan with all fields including generated ID")
@Validated
public class MaintenancePlan {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY, description = "Internal ID, set by Cumulocity", example = "1")
    @NotNull
    @NonNull
    private Integer id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "The name of the maintenance plan", example = "Monthly Equipment Check")
    @NotNull
    @NonNull
    private String name;

    @Schema(description = "Detailed description of the maintenance plan")
    private String description;

    @Schema(description = "Maintenance text for alarm")
    private String notificationText;

    @Schema(description = "Maintenance type for alarm")
    private String notificationType;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Start date", example = "2025-01-01T09:00:00Z")
    @NotNull
    @NonNull
    private DateTime startDate;

    @Schema(description = "End date", example = "2025-12-31T17:00:00Z")
    @NotNull
    @NonNull
    private DateTime endDate;

    @Schema(description = "Is active", example = "true")
    private Boolean active;

    @Schema(description = "List of maintenance triggers")
    @Valid
    private List<MaintenanceTrigger> on;

    // --- NEW FIELDS FOR AI ---
    @Schema(description = "Frequency of the maintenance")
    private String frequency;

    @Schema(description = "ID of the equipment")
    private String equipmentId;

    @Schema(description = "List of required skills")
    private List<String> requiredSkills;

    @Schema(description = "List of specific maintenance tasks")
    private List<Object> tasks;
}