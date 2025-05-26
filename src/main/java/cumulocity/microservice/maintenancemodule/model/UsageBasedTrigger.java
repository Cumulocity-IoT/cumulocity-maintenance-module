package cumulocity.microservice.maintenancemodule.model;

/**
 * Usage-based maintenance trigger based on counters and thresholds
 * 
 * @author APES
 */
public class UsageBasedTrigger implements MaintenanceTrigger {
    private String type = "Usage-based";
    private UsageCounter counter;

    // Default constructor
    public UsageBasedTrigger() {}

    // Constructor
    public UsageBasedTrigger(UsageCounter counter) {
        this.counter = counter;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UsageCounter getCounter() {
        return counter;
    }

    public void setCounter(UsageCounter counter) {
        this.counter = counter;
    }

    @Override
    public String toString() {
        return "UsageBasedTrigger{" +
                "type='" + type + '\'' +
                ", counter=" + counter +
                '}';
    }
}
