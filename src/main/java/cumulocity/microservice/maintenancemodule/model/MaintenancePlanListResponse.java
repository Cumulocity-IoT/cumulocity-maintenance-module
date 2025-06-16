package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

/**
 * Response wrapper for paginated maintenance plan list results.
 * Provides maintenance plan data with pagination information aligned with Cumulocity patterns.
 * 
 * @author APES
 * @since 1.0.0
 */
public class MaintenancePlanListResponse {
    private List<MaintenancePlan> maintenancePlans;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalElements;

    /**
     * Creates a new empty MaintenancePlanListResponse.
     * 
     * @since 1.0.0
     */
    public MaintenancePlanListResponse() {}

    /**
     * Creates a new MaintenancePlanListResponse with data and pagination.
     * 
     * @param maintenancePlans the list of maintenance plans
     * @param currentPage the current page number (0-based)
     * @param pageSize the number of items per page
     * @param totalPages the total number of pages
     * @param totalElements the total number of elements
     * @since 1.0.0
     */
    public MaintenancePlanListResponse(List<MaintenancePlan> maintenancePlans, Integer currentPage, 
                                       Integer pageSize, Integer totalPages, Long totalElements) {
        this.maintenancePlans = maintenancePlans;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    /**
     * Returns the list of maintenance plans.
     * 
     * @return the maintenance plans list
     * @since 1.0.0
     */
    public List<MaintenancePlan> getMaintenancePlans() {
        return maintenancePlans;
    }

    /**
     * Sets the list of maintenance plans.
     * 
     * @param maintenancePlans the maintenance plans to set
     * @since 1.0.0
     */
    public void setMaintenancePlans(List<MaintenancePlan> maintenancePlans) {
        this.maintenancePlans = maintenancePlans;
    }

    /**
     * Returns the current page number (0-based).
     * 
     * @return the current page number
     * @since 1.0.0
     */
    public Integer getCurrentPage() {
        return currentPage;
    }

    /**
     * Sets the current page number.
     * 
     * @param currentPage the current page number to set
     * @since 1.0.0
     */
    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Returns the page size.
     * 
     * @return the number of items per page
     * @since 1.0.0
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page size.
     * 
     * @param pageSize the page size to set
     * @since 1.0.0
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Returns the total number of pages.
     * 
     * @return the total pages count
     * @since 1.0.0
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the total number of pages.
     * 
     * @param totalPages the total pages to set
     * @since 1.0.0
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Returns the total number of elements across all pages.
     * 
     * @return the total elements count
     * @since 1.0.0
     */
    public Long getTotalElements() {
        return totalElements;
    }

    /**
     * Sets the total number of elements.
     * 
     * @param totalElements the total elements to set
     * @since 1.0.0
     */
    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public String toString() {
        return "MaintenancePlanListResponse{" +
                "maintenancePlans=" + maintenancePlans +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", totalElements=" + totalElements +
                '}';
    }
}
