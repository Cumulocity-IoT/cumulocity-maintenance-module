package cumulocity.microservice.maintenancemodule.model;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the subscription state for a device within a usage-based maintenance plan.
 * Tracks the Notification 2.0 subscription details and accumulated counter values.
 * 
 * @author APES
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Device subscription state for usage-based maintenance")
@Validated
public class DeviceSubscriptionInfo {

    @Schema(required = true, description = "The subscribed device ID", example = "12345")
    private String deviceId;

    @Schema(required = true, description = "Notification 2.0 subscription name", example = "mp_1234_usage")
    private String subscriptionName;

    @Schema(description = "Running accumulated counter value", example = "150.5")
    @Builder.Default
    private Double currentAccumulatedValue = 0.0;

    @Schema(description = "Timestamp when counter was last reset")
    private DateTime lastResetTimestamp;

    @Schema(description = "Timestamp when subscription was created")
    private DateTime createdTimestamp;

    @Schema(description = "Whether the subscription is currently active", example = "true")
    @Builder.Default
    private Boolean active = true;

    /**
     * Resets the accumulator to zero and updates the reset timestamp.
     * 
     * @since 1.0.0
     */
    public void resetAccumulator() {
        this.currentAccumulatedValue = 0.0;
        this.lastResetTimestamp = new DateTime();
    }

    /**
     * Resets the accumulator to a specific cycle value and updates the reset timestamp.
     * 
     * @param cycleValue the value to reset the accumulator to
     * @since 1.0.0
     */
    public void resetAccumulator(Double cycleValue) {
        this.currentAccumulatedValue = cycleValue != null ? cycleValue : 0.0;
        this.lastResetTimestamp = new DateTime();
    }

    /**
     * Adds a value to the accumulator.
     * 
     * @param value the value to add to the current accumulated value
     * @return the new accumulated value
     * @since 1.0.0
     */
    public Double addToAccumulator(Double value) {
        if (value != null) {
            this.currentAccumulatedValue += value;
        }
        return this.currentAccumulatedValue;
    }
}
