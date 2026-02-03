package cumulocity.microservice.maintenancemodule.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Defines a specific trigger condition for maintenance")
public class MaintenanceTrigger {

    @Schema(description = "Type of the trigger (e.g., TIME, USAGE, CONDITION)", example = "TIME")
    private String type;

    @Schema(description = "Value for the trigger threshold", example = "100")
    private String value;

    @Schema(description = "Unit for the trigger (e.g., HOURS, DAYS, COUNTS)", example = "DAYS")
    private String unit;
}