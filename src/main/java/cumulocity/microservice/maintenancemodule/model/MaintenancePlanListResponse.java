package cumulocity.microservice.maintenancemodule.model;

import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Response wrapper for paginated maintenance plan list results.
 * Provides maintenance plan data with pagination information aligned with Cumulocity patterns.
 * 
 * @author APES
 * @since 1.0.0
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing a list of maintenance plans")
public class MaintenancePlanListResponse {

    @Schema(description = "List of maintenance plans")
    @JsonProperty("plans")
    private List<MaintenancePlan> plans = new ArrayList<>();

    // Manual Setter to guarantee compilation
    public void setPlans(List<MaintenancePlan> plans) {
        this.plans = plans;
    }

    @Schema(required = true, description = "Number of items per page", example = "10")
    @NotNull
    @NonNull
    private Integer pageSize;

    @Schema(required = false, description = "Total number of pages", example = "5")
    private Integer totalPages;

    @Schema(description = "Total number of elements across all pages", example = "42")
    private Long totalElements;
    // Manual Getter
    public List<MaintenancePlan> getPlans() {
        return plans;
    }
}
