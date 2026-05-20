package cumulocity.microservice.maintenancemodule.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Usage-based maintenance trigger based on counters and thresholds
 * 
 * @author APES
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Usage-based maintenance trigger based on counters and thresholds")
@Validated
public class UsageBasedTrigger implements MaintenanceTrigger {

    @Schema(required = true, description = "Type of trigger", example = "Usage-based")
    @NotNull
    @NonNull
    private String type = "Usage-based";

    @Schema(required = true, description = "Usage counter configuration")
    @NotNull
    @NonNull
    @Valid
    private UsageCounter counter;
}
