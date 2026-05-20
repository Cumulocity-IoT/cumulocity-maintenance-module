package cumulocity.microservice.maintenancemodule.model;

import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Subscription details for Usage-based and Condition-based triggers
 * 
 * @author APES
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Subscription details for Usage-based and Condition-based triggers")
@Validated
public class Subscription {

    @Schema(required = true, description = "API endpoint to subscribe to", example = "measurements")
    @NotNull
    @NonNull
    private String api;

    @Schema(description = "Type filter for subscription", example = "c8y_Temperature")
    private String typeFilter;
}
