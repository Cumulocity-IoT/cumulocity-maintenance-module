package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a schema for creating a new maintenance plan, excluding the auto-generated ID.
 * Provides functionality to create maintenance schedules for Cumulocity devices.
 * 
 * @author APES
 * @since 1.0.0
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Schema for creating a new maintenance plan, excluding the auto-generated ID")
@Validated
public class MaintenancePlanCreate {

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
    @NotNull
    @NonNull
    private DateTime startDate;

    @Schema(description = "End date and time for the maintenance", example = "2025-12-31T17:00:00Z")
    @NotNull
    @NonNull
    private DateTime endDate;

    @Schema(description = "Indicates whether the maintenance plan is active", example = "true")
    private Boolean active;

    @Schema(description = "List of maintenance triggers")
    @Valid
    private List<MaintenanceTrigger> on;
}
