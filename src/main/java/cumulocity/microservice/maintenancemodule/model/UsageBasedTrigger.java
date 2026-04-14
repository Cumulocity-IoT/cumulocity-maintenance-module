package cumulocity.microservice.maintenancemodule.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Usage-based maintenance trigger based on counters and thresholds
 * * @author APES
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Usage-based maintenance trigger based on counters and thresholds")
@Validated
public class UsageBasedTrigger {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Type of trigger", example = "Usage-based")
    @NotNull
    private String type = "Usage-based";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Usage counter configuration")
    @NotNull
    @Valid
    private UsageCounter counter;
}