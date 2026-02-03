package cumulocity.microservice.maintenancemodule.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Schema(description = "Response containing a list of maintenance plans")
public class MaintenancePlanListResponse {

    // Manual Setter to guarantee compilation
    // Manual Getter
    @Schema(description = "List of maintenance plans")
    @JsonProperty("plans")
    private List<MaintenancePlan> plans = new ArrayList<>();
    @Schema(description = "List of maintenance plans")
    @JsonProperty("maintenancePlans")
    private List<MaintenancePlan> maintenancePlans = new ArrayList<>();

    @Schema(description = "Current page number (0-based)", example = "0")
    private Integer currentPage;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Number of items per page", example = "10")
    @NotNull
    private Integer pageSize;

    @Schema(description = "Total number of pages", example = "5")
    private Integer totalPages;

}