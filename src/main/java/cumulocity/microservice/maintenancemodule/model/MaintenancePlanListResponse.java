package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
@Schema(description = "Response wrapper for paginated maintenance plan list results")
@Validated
public class MaintenancePlanListResponse {

    @Schema(required = true, description = "List of maintenance plans")
    @NotNull
    @NonNull
    @Valid
    private List<MaintenancePlan> maintenancePlans;

    @Schema(required = true, description = "Current page number (0-based)", example = "0")
    @NotNull
    @NonNull
    private Integer currentPage;

    @Schema(required = true, description = "Number of items per page", example = "10")
    @NotNull
    @NonNull
    private Integer pageSize;

    @Schema(required = true, description = "Total number of pages", example = "5")
    @NotNull
    @NonNull
    private Integer totalPages;

    @Schema(description = "Total number of elements across all pages", example = "42")
    private Long totalElements;
}
