package cumulocity.microservice.maintenancemodule.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Single counter for usage-based maintenance defining criteria for triggers
 * 
 * @author APES
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Single counter for usage-based maintenance defining criteria for triggers")
@Validated
public class UsageCounter {

    @Schema(required = true, description = "Subscription details")
    @NotNull
    @NonNull
    @Valid
    private Subscription subscription;

    @Schema(required = true, description = "Value fragment to monitor", example = "c8y_Temperature.T.value")
    @NotNull
    @NonNull
    private String valueFragment;

    @Schema(description = "Cycle value for counter reset", example = "1000")
    private Integer cycleValue;

    @Schema(description = "Threshold value to trigger maintenance", example = "800")
    private Integer thresholdValue;
}
