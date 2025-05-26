package cumulocity.microservice.maintenancemodule.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base interface for maintenance triggers with different types:
 * Time-based, Usage-based, and Condition-based
 * 
 * @author APES
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeBasedTrigger.class, name = "Time-based"),
    @JsonSubTypes.Type(value = UsageBasedTrigger.class, name = "Usage-based"),
    @JsonSubTypes.Type(value = ConditionBasedTrigger.class, name = "Condition-based")
})
public interface MaintenanceTrigger {
    String getType();
}
