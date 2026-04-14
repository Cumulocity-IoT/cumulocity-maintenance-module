package cumulocity.microservice.maintenancemodule.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cumulocity.microservice.maintenancemodule.validation.ValidCron;
import org.springframework.validation.annotation.Validated;

/**
 * Time-based maintenance trigger definition.
 * Supports both ISO 8601 intervals and Cron expressions.
 *
 * @author APES
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Time-based maintenance trigger definition")
@Validated
public class TimeBasedTrigger {

    @Schema(
            description = "Cron expression (Spring/Quartz 6-field format). Example: '0 0 9 * * 1' (Monday at 9am)",
            example = "0 0 9 * * 1"
    )
    @ValidCron
    @NotNull
    @NonNull
    private String cronExpression;
}