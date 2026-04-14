package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Condition-based maintenance trigger with multiple conditions
 * * @author APES
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Condition-based maintenance trigger with multiple conditions")
@Validated
public class ConditionBasedTrigger {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Type of trigger", example = "Condition-based")
    @NotNull
    private String type;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "List of maintenance conditions")
    @NotNull
    @Valid
    private List<MaintenanceCondition> conditions;
}