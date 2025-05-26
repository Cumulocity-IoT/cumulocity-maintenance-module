package cumulocity.microservice.maintenancemodule.model;

/**
 * Single counter for usage-based maintenance defining criteria for triggers
 * 
 * @author APES
 */
public class UsageCounter {
    private Subscription subscription;
    private String valueFragment;
    private Integer cycleValue;
    private Integer thresholdValue;

    // Default constructor
    public UsageCounter() {}

    // Constructor
    public UsageCounter(Subscription subscription, String valueFragment, Integer cycleValue, Integer thresholdValue) {
        this.subscription = subscription;
        this.valueFragment = valueFragment;
        this.cycleValue = cycleValue;
        this.thresholdValue = thresholdValue;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getValueFragment() {
        return valueFragment;
    }

    public void setValueFragment(String valueFragment) {
        this.valueFragment = valueFragment;
    }

    public Integer getCycleValue() {
        return cycleValue;
    }

    public void setCycleValue(Integer cycleValue) {
        this.cycleValue = cycleValue;
    }

    public Integer getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(Integer thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    @Override
    public String toString() {
        return "UsageCounter{" +
                "subscription=" + subscription +
                ", valueFragment='" + valueFragment + '\'' +
                ", cycleValue=" + cycleValue +
                ", thresholdValue=" + thresholdValue +
                '}';
    }
}
