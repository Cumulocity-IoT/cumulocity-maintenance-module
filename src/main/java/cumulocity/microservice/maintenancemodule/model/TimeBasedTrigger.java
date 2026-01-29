package cumulocity.microservice.maintenancemodule.model;

import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import cumulocity.microservice.maintenancemodule.validation.ValidCron;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Time-based maintenance trigger using cron expressions
 * 
 * @author APES
 * @since 1.0.0
 * @version 1.1.0
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Time-based maintenance trigger definition using cron expressions")
@Validated
public class TimeBasedTrigger {

    @Schema(required = true, description = "Cron expression for scheduling (e.g., '0 9 * * 1' for every Monday at 9:00 AM)", example = "0 9 * * 1")
    @NotNull
    @NonNull
    @ValidCron
    private String cronExpression;
}
