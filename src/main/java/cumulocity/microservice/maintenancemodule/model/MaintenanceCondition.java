package cumulocity.microservice.maintenancemodule.model;

/**
 * Individual condition for condition-based maintenance triggers
 * 
 * @author APES
 */
public class MaintenanceCondition {
    private Subscription subscription;
    private String valueFragment;
    private String operator;
    private Object value;

    // Default constructor
    public MaintenanceCondition() {}

    // Constructor
    public MaintenanceCondition(Subscription subscription, String valueFragment, String operator, Object value) {
        this.subscription = subscription;
        this.valueFragment = valueFragment;
        this.operator = operator;
        this.value = value;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MaintenanceCondition{" +
                "subscription=" + subscription +
                ", valueFragment='" + valueFragment + '\'' +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }
}
