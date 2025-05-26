package cumulocity.microservice.maintenancemodule.model;

/**
 * Subscription details for Usage-based and Condition-based triggers
 * 
 * @author APES
 */
public class Subscription {
    private String api;
    private String typeFilter;

    // Default constructor
    public Subscription() {}

    // Constructor
    public Subscription(String api, String typeFilter) {
        this.api = api;
        this.typeFilter = typeFilter;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getTypeFilter() {
        return typeFilter;
    }

    public void setTypeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "api='" + api + '\'' +
                ", typeFilter='" + typeFilter + '\'' +
                '}';
    }
}
