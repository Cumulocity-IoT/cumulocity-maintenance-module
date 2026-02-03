package cumulocity.microservice.maintenancemodule.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Time-based maintenance trigger using ISO 8601 duration intervals
 * * @author APES
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Time-based maintenance trigger definition using ISO 8601 duration intervals")
@Validated
public class TimeBasedTrigger {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "ISO 8601 duration interval", example = "PT1H")
    @NotNull
    private String interval;
}