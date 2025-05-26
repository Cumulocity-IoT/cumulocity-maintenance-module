package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

/**
 * Response wrapper for paginated list results
 * 
 * @author APES
 */
public class MaintenancePlanListResponse {
    private List<MaintenancePlan> data;
    private Pagination pagination;

    // Default constructor
    public MaintenancePlanListResponse() {}

    // Constructor
    public MaintenancePlanListResponse(List<MaintenancePlan> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public List<MaintenancePlan> getData() {
        return data;
    }

    public void setData(List<MaintenancePlan> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return "MaintenancePlanListResponse{" +
                "data=" + data +
                ", pagination=" + pagination +
                '}';
    }
}
