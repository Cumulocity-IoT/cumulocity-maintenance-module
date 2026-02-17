package cumulocity.microservice.maintenancemodule.model;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cumulocity.microservice.maintenancemodule.validation.ValidCron;

/**
 * Time-based maintenance trigger definition.
 * Supports both ISO 8601 intervals and Cron expressions.
 *
 * @author APES
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Time-based maintenance trigger definition")
public class TimeBasedTrigger {

    @Schema(
            description = "ISO 8601 duration interval (e.g., 'PT1H' for 1 hour)",
            example = "PT1H"
    )
    private String interval;

    @Schema(
            description = "Cron expression (Spring/Quartz 6-field format). Example: '0 0 9 * * 1' (Monday at 9am)",
            example = "0 0 9 * * 1"
    )
    @ValidCron
    private String cronExpression;
}