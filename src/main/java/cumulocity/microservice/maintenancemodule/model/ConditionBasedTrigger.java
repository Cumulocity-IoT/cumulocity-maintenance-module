package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

/**
 * Condition-based maintenance trigger with multiple conditions
 * 
 * @author APES
 */
public class ConditionBasedTrigger implements MaintenanceTrigger {
    private String type = "Condition-based";
    private List<MaintenanceCondition> conditions;

    // Default constructor
    public ConditionBasedTrigger() {}

    // Constructor
    public ConditionBasedTrigger(List<MaintenanceCondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MaintenanceCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<MaintenanceCondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return "ConditionBasedTrigger{" +
                "type='" + type + '\'' +
                ", conditions=" + conditions +
                '}';
    }
}
