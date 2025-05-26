package cumulocity.microservice.maintenancemodule.model;

/**
 * Time-based maintenance trigger using ISO 8601 duration intervals
 * 
 * @author APES
 */
public class TimeBasedTrigger implements MaintenanceTrigger {
    private String type = "Time-based";
    private String interval;

    // Default constructor
    public TimeBasedTrigger() {}

    // Constructor
    public TimeBasedTrigger(String interval) {
        this.interval = interval;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "TimeBasedTrigger{" +
                "type='" + type + '\'' +
                ", interval='" + interval + '\'' +
                '}';
    }
}
