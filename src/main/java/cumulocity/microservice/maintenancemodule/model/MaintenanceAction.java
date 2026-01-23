package cumulocity.microservice.maintenancemodule.model;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents a maintenance action for a device")
@Validated
public class MaintenanceAction {

    public enum MaintenanceStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    @Schema(required = true, description = "Device Internal ID", example = "1234")
    @NotNull
    @NonNull
    private String deviceId;

    @Schema(required = true, description = "Maintenance status", example = "SCHEDULED")
    @NotNull
    @NonNull
    private MaintenanceStatus status;

    @Schema(description = "Additional notification message", example = "Maintenance scheduled for next week")
    private String notification;
}
