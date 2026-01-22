package cumulocity.microservice.maintenancemodule.model;

import jakarta.validation.constraints.NotNull;

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
@Schema(description = "Time-based maintenance trigger definition using ISO 8601 duration intervals")
@Validated
public class TimeBasedTrigger {

    @Schema(required = true, description = "ISO 8601 duration interval", example = "PT1H")
    @NotNull
    @NonNull
    private String interval;
}
