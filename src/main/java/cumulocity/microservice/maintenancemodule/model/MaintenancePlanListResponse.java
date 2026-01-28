package cumulocity.microservice.maintenancemodule.model;

import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing a list of maintenance plans")
public class MaintenancePlanListResponse {

    // Manual Setter to guarantee compilation
    // Manual Getter
    @Schema(description = "List of maintenance plans")
    @JsonProperty("plans")
    private List<MaintenancePlan> plans = new ArrayList<>();

}