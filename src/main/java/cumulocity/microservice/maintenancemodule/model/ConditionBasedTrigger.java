package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Condition-based maintenance trigger with multiple conditions
 * 
 * @author APES
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Condition-based maintenance trigger with multiple conditions")
@Validated
public class ConditionBasedTrigger implements MaintenanceTrigger {

    @Schema(required = true, description = "Type of trigger", example = "Condition-based")
    @NotNull
    @NonNull
    private String type;

    @Schema(required = true, description = "List of maintenance conditions")
    @NotNull
    @NonNull
    @Valid
    private List<MaintenanceCondition> conditions;
}
