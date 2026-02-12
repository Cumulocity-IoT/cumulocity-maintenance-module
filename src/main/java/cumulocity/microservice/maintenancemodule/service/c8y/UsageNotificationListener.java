package cumulocity.microservice.maintenancemodule.service.c8y;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.notification2.Notification;
import com.cumulocity.sdk.client.notification2.NotificationListener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cumulocity.microservice.maintenancemodule.model.DeviceSubscriptionInfo;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.UsageCounter;

/**
 * Notification listener for usage-based maintenance.
 * Receives notifications from Notification 2.0 API and evaluates usage counter thresholds.
 * Creates alarms or events when maintenance thresholds are reached.
 * 
 * @author APES
 * @since 1.0.0
 */
@Component
public class UsageNotificationListener implements NotificationListener {
    private static final Logger log = LoggerFactory.getLogger(UsageNotificationListener.class);

    private final MaintenancePlanService maintenancePlanService;
    private final UsageSubscriptionService usageSubscriptionService;
    private final AlarmApi alarmApi;
    private final EventApi eventApi;
    private final InventoryApi inventoryApi;
    private final ObjectMapper objectMapper;
    private final MicroserviceSubscriptionsService subscriptions;

    /**
     * Constructs the UsageNotificationListener with required dependencies.
     * Uses @Lazy for UsageSubscriptionService to avoid circular dependency.
     * 
     * @param maintenancePlanService service for retrieving maintenance plans
     * @param usageSubscriptionService service for managing subscriptions (lazy loaded)
     * @param alarmApi Cumulocity Alarm API
     * @param eventApi Cumulocity Event API
     * @param inventoryApi Cumulocity Inventory API
     * @since 1.0.0
     */
    public UsageNotificationListener(MaintenancePlanService maintenancePlanService,
                                      @Lazy UsageSubscriptionService usageSubscriptionService,
                                      AlarmApi alarmApi,
                                      EventApi eventApi,
                                      InventoryApi inventoryApi,
                                      MicroserviceSubscriptionsService subscriptions) {
        this.maintenancePlanService = maintenancePlanService;
        this.usageSubscriptionService = usageSubscriptionService;
        this.alarmApi = alarmApi;
        this.eventApi = eventApi;
        this.inventoryApi = inventoryApi;
        this.subscriptions = subscriptions;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles incoming notifications from Notification 2.0 API.
     * Extracts the value from the notification, updates the accumulator,
     * and creates alarms/events when threshold is reached.
     * 
     * @param message the notification message
     * @param subscriptionName the name of the subscription that received the notification
     * @param tenantId the tenant ID
     * @param deviceId the device ID that generated the notification
     * @since 1.0.0
     */
    @Override
    public void onMessage(Notification message, String subscriptionName, String tenantId, String deviceId) {
        log.debug("Received notification from subscription '{}' for device {} in tenant {}", 
                subscriptionName, deviceId, tenantId);
        subscriptions.runForTenant(tenantId, new Runnable() {
            @Override
            public void run() {
                processUsageBasedRule(message, subscriptionName, tenantId, deviceId);              
            }
        });

    }

    private void processUsageBasedRule(Notification message, String subscriptionName, String tenantId, String deviceId) {
        try {
            // Extract plan ID from subscription name
            String planId = MaintenancePlanMapper.extractPlanIdFromSubscriptionName(subscriptionName);
            if (planId == null) {
                log.warn("Could not extract plan ID from subscription name: {}", subscriptionName);
                return;
            }

            // Get the maintenance plan
            MaintenancePlan maintenancePlan = maintenancePlanService.getMaintenancePlan(planId);
            if (maintenancePlan == null) {
                log.warn("Maintenance plan not found for ID: {}", planId);
                return;
            }

            // Check if plan is active
            if (maintenancePlan.getActive() == null || !maintenancePlan.getActive()) {
                log.debug("Skipping notification - maintenance plan {} is not active", planId);
                return;
            }

            // Get usage counter configuration
            if (maintenancePlan.getOnUsage() == null || maintenancePlan.getOnUsage().getCounter() == null) {
                log.warn("No usage counter configured for plan {}", planId);
                return;
            }

            UsageCounter counter = maintenancePlan.getOnUsage().getCounter();

            // Extract value from notification message
            Double extractedValue = extractValueFromNotification(message, counter.getValueFragment());
            if (extractedValue == null) {
                log.debug("Could not extract value from notification using fragment path: {}", 
                        counter.getValueFragment());
                return;
            }

            log.debug("Extracted value {} from notification for device {} using fragment {}", 
                    extractedValue, deviceId, counter.getValueFragment());

            // Update accumulator
            DeviceSubscriptionInfo subscriptionInfo = usageSubscriptionService.updateAccumulator(
                    planId, deviceId, extractedValue);
            
            if (subscriptionInfo == null) {
                log.warn("No subscription info found for device {} in plan {}", deviceId, planId);
                return;
            }

            Double currentValue = subscriptionInfo.getCurrentAccumulatedValue();
            Integer threshold = counter.getThresholdValue();

            log.info("Device {} accumulator: {} / {} (threshold)", deviceId, currentValue, threshold);

            // Check if threshold is reached
            if (threshold != null && currentValue >= threshold) {
                log.info("Threshold reached for device {} in plan {} (current: {}, threshold: {})", 
                        deviceId, planId, currentValue, threshold);
                
                // Create notification based on plan configuration
                createMaintenanceNotification(maintenancePlan, deviceId, currentValue);

                // Reset accumulator
                Integer cycleValue = counter.getCycleValue();
                usageSubscriptionService.resetAccumulator(planId, deviceId, cycleValue);
            }

        } catch (Exception e) {
            log.error("Error processing notification from subscription '{}': {}", 
                    subscriptionName, e.getMessage(), e);
        }
    }

    /**
     * Extracts a value from the notification message using a dot-separated fragment path.
     * For example, "c8y_Temperature.T.value" would navigate to message.c8y_Temperature.T.value
     * 
     * @param message the notification message
     * @param fragmentPath the dot-separated path to the value
     * @return the extracted value as Double, or null if not found
     * @since 1.0.0
     */
    private Double extractValueFromNotification(Notification message, String fragmentPath) {
        if (message == null || fragmentPath == null || fragmentPath.isEmpty()) {
            return null;
        }

        try {
            // Parse the notification payload as JSON
            String payload = message.getPayload();
            JsonNode rootNode = objectMapper.readTree(payload);
            
            // Navigate the path
            String[] pathParts = fragmentPath.split("\\.");
            JsonNode currentNode = rootNode;
            
            for (String part : pathParts) {
                if (currentNode == null || !currentNode.has(part)) {
                    log.debug("Path element '{}' not found in notification", part);
                    return null;
                }
                currentNode = currentNode.get(part);
            }

            // Extract numeric value
            if (currentNode != null && currentNode.isNumber()) {
                return currentNode.asDouble();
            } else if (currentNode != null && currentNode.isTextual()) {
                try {
                    return Double.parseDouble(currentNode.asText());
                } catch (NumberFormatException e) {
                    log.debug("Could not parse value as number: {}", currentNode.asText());
                    return null;
                }
            }

        } catch (Exception e) {
            log.debug("Error extracting value from notification: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Creates a maintenance notification (alarm or event) based on the plan configuration.
     * 
     * @param maintenancePlan the maintenance plan
     * @param deviceId the device ID
     * @param currentValue the current accumulated value
     * @since 1.0.0
     */
    private void createMaintenanceNotification(MaintenancePlan maintenancePlan, 
                                                String deviceId, 
                                                Double currentValue) {
        switch (maintenancePlan.getNotificationClass()) {
            case ALARM:
                createMaintenanceAlarm(maintenancePlan, deviceId, currentValue);
                break;
            case EVENT:
                createMaintenanceEvent(maintenancePlan, deviceId, currentValue);
                break;
            default:
                log.warn("Unknown notification class: {}", maintenancePlan.getNotificationClass());
        }
    }

    /**
     * Creates a maintenance alarm for the device.
     * 
     * @param maintenancePlan the maintenance plan
     * @param deviceId the device ID
     * @param currentValue the accumulated value that triggered the alarm
     * @since 1.0.0
     */
    private void createMaintenanceAlarm(MaintenancePlan maintenancePlan, 
                                         String deviceId, 
                                         Double currentValue) {
        try {
            ManagedObjectRepresentation device = inventoryApi.get(GId.asGId(deviceId));
            
            AlarmRepresentation alarm = new AlarmRepresentation();
            alarm.setSource(device);
            alarm.setType(MaintenancePlanMapper.ALARM_TYPE + maintenancePlan.getId());
            alarm.setStatus("ACTIVE");
            alarm.setSeverity(maintenancePlan.getNotificationSeverity() != null 
                    ? maintenancePlan.getNotificationSeverity().name() 
                    : "MAJOR");
            alarm.setText(buildNotificationText(maintenancePlan, currentValue));
            alarm.setDateTime(new DateTime());
            alarm.set(maintenancePlan.getId(), MaintenancePlanMapper.MAINTENANCE_PLAN_ID);
            alarm.set(currentValue, "mp_AccumulatedValue");
            alarm.set("USAGE_BASED", "mp_TriggerType");

            AlarmRepresentation createdAlarm = alarmApi.create(alarm);
            log.info("Created maintenance alarm {} for device {} in plan {}", 
                    createdAlarm.getId().getValue(), deviceId, maintenancePlan.getId());

        } catch (Exception e) {
            log.error("Failed to create maintenance alarm for device {} in plan {}: {}", 
                    deviceId, maintenancePlan.getId(), e.getMessage(), e);
        }
    }

    /**
     * Creates a maintenance event for the device.
     * 
     * @param maintenancePlan the maintenance plan
     * @param deviceId the device ID
     * @param currentValue the accumulated value that triggered the event
     * @since 1.0.0
     */
    private void createMaintenanceEvent(MaintenancePlan maintenancePlan, 
                                         String deviceId, 
                                         Double currentValue) {
        try {
            ManagedObjectRepresentation device = inventoryApi.get(GId.asGId(deviceId));
            
            EventRepresentation event = new EventRepresentation();
            event.setSource(device);
            event.setType(MaintenancePlanMapper.EVENT_TYPE_MAINTENANCE + maintenancePlan.getId());
            event.setText(buildNotificationText(maintenancePlan, currentValue));
            event.setDateTime(new DateTime());
            event.set(maintenancePlan.getId(), MaintenancePlanMapper.MAINTENANCE_PLAN_ID);
            event.set(currentValue, "mp_AccumulatedValue");
            event.set("USAGE_BASED", "mp_TriggerType");

            EventRepresentation createdEvent = eventApi.create(event);
            log.info("Created maintenance event {} for device {} in plan {}", 
                    createdEvent.getId().getValue(), deviceId, maintenancePlan.getId());

        } catch (Exception e) {
            log.error("Failed to create maintenance event for device {} in plan {}: {}", 
                    deviceId, maintenancePlan.getId(), e.getMessage(), e);
        }
    }

    /**
     * Builds the notification text for alarms and events.
     * 
     * @param maintenancePlan the maintenance plan
     * @param currentValue the accumulated value that triggered the notification
     * @return the formatted notification text
     * @since 1.0.0
     */
    private String buildNotificationText(MaintenancePlan maintenancePlan, Double currentValue) {
        if (maintenancePlan.getNotificationText() != null && !maintenancePlan.getNotificationText().isEmpty()) {
            return maintenancePlan.getNotificationText();
        }
        return String.format("Usage-based maintenance triggered for plan '%s'. Accumulated value: %.2f", 
                maintenancePlan.getName(), currentValue);
    }
}
