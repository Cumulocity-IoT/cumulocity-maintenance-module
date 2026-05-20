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
 * Individual condition for condition-based maintenance triggers
 * 
 * @author APES
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Individual condition for condition-based maintenance triggers")
@Validated
public class MaintenanceCondition {

    @Schema(required = true, description = "Subscription details")
    @NotNull
    @NonNull
    @Valid
    private Subscription subscription;

    @Schema(required = true, description = "Value fragment to evaluate", example = "c8y_Temperature.T.value")
    @NotNull
    @NonNull
    private String valueFragment;

    @Schema(required = true, description = "Comparison operator", example = "gt", allowableValues = {"eq", "ne", "gt", "lt", "gte", "lte"})
    @NotNull
    @NonNull
    private String operator;

    @Schema(required = true, description = "Value to compare against", example = "25.0")
    @NotNull
    @NonNull
    private Object value;
}
