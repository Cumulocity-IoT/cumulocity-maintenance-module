package cumulocity.microservice.maintenancemodule.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response wrapper for paginated maintenance plan list results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing a list of maintenance plans")
@Validated
public class MaintenancePlanListResponse {

    @Schema(description = "List of maintenance plans")
    @JsonProperty("maintenancePlans")
    @NotNull
    @Valid
    private List<MaintenancePlan> maintenancePlans = new ArrayList<>();

    @Schema(description = "Current page number (0-based)", example = "0")
    @NotNull
    private Integer currentPage;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Number of items per page", example = "10")
    @NotNull
    private Integer pageSize;

    @Schema(description = "Total number of pages", example = "5")
    @NotNull
    private Integer totalPages;

    @Schema(description = "Total number of elements across all pages", example = "42")
    @NotNull
    private Long totalElements;
}