package cumulocity.microservice.maintenancemodule.model;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;    
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Action performed on a maintenance plan (e.g. starting or completing work)")
@Validated
public class MaintenanceAction {

    @Schema(description = "The ID of the device being maintained", example = "10200")
    @NotNull
    @NonNull
    private String deviceId;

    @Schema(description = "The current status of the maintenance", example = "IN_PROGRESS")
    @NotNull
    @NonNull
    private MaintenanceStatus status;

    @Schema(description = "Optional text for the event or alarm", example = "Technician arrived on site")
    @NotNull
    @NonNull
    private String notification;

    public enum MaintenanceStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}