package cumulocity.microservice.maintenancemodule.model;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Time-based maintenance trigger using ISO 8601 duration intervals
 * 
 * @author APES
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Time-based maintenance trigger using ISO 8601 duration intervals")
@Validated
public class TimeBasedTrigger implements MaintenanceTrigger {

    @Schema(required = true, description = "Type of trigger", example = "Time-based")
    @NotNull
    @NonNull
    private String type = "Time-based";

    @Schema(required = true, description = "ISO 8601 duration interval", example = "PT1H")
    @NotNull
    @NonNull
    private String interval;
}
