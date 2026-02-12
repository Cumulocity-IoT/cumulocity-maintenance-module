package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.notification2.DeviceContextTargetApi;
import com.cumulocity.sdk.client.notification2.NotificationListener;
import com.cumulocity.sdk.client.notification2.Notifications2Api;
import com.cumulocity.sdk.client.notification2.Subscription;

import cumulocity.microservice.maintenancemodule.model.DeviceSubscriptionInfo;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.UsageCounter;

/**
 * Service for managing Notification 2.0 subscriptions for usage-based maintenance.
 * Handles creating, removing, and tracking subscriptions for devices within maintenance plans.
 * 
 * @author APES
 * @since 1.0.0
 */
@Service
public class UsageSubscriptionService {
    private static final Logger log = LoggerFactory.getLogger(UsageSubscriptionService.class);

    private final Notifications2Api notifications2Api;
    private final InventoryApi inventoryApi;
    private final NotificationListener notificationListener;

    @Value("${notification2.subscriber.id:maintenance-module-subscriber}")
    private String subscriberId;

    /**
     * Constructs the UsageSubscriptionService with required dependencies.
     * 
     * @param notifications2Api the Notification 2.0 API for managing subscriptions
     * @param inventoryApi the Inventory API for updating managed objects
     * @param notificationListener the listener to receive notifications
     * @since 1.0.0
     */
    public UsageSubscriptionService(Notifications2Api notifications2Api, 
                                     InventoryApi inventoryApi,
                                     NotificationListener notificationListener) {
        this.notifications2Api = notifications2Api;
        this.inventoryApi = inventoryApi;
        this.notificationListener = notificationListener;
    }

    /**
     * Creates a Notification 2.0 subscription for a device within a maintenance plan.
     * Subscribes to the API specified in the usage counter configuration.
     * 
     * @param maintenancePlan the maintenance plan containing the usage trigger configuration
     * @param device the device to subscribe to
     * @return the created DeviceSubscriptionInfo, or null if creation failed
     * @since 1.0.0
     */
    public DeviceSubscriptionInfo createSubscription(MaintenancePlan maintenancePlan, 
                                                      ManagedObjectRepresentation device) {
        if (maintenancePlan.getOnUsage() == null || maintenancePlan.getOnUsage().getCounter() == null) {
            log.warn("Cannot create subscription - no usage counter configured for plan {}", 
                    maintenancePlan.getId());
            return null;
        }

        UsageCounter counter = maintenancePlan.getOnUsage().getCounter();
        String subscriptionName = MaintenancePlanMapper.generateSubscriptionName(maintenancePlan.getId());
        String deviceId = device.getId().getValue();
        String tenantId = extractTenantId();

        try {
            // Build the subscription based on the counter's subscription configuration
            DeviceContextTargetApi targetApi = mapApiToDeviceContextTargetApi(
                    counter.getSubscription().getApi());
            
            Subscription.Builder builder = Subscription.Builder.get()
                    .withId(subscriptionName, subscriberId)
                    .withTenantId(tenantId)
                    .withDeviceContextTargetApis(deviceId, targetApi)
                    .withShared(false)
                    .withPersistent(true);

            // Add type filter if specified
            if (counter.getSubscription().getTypeFilter() != null) {
                builder = builder.withTypeFilter(counter.getSubscription().getTypeFilter());
            }

            Subscription subscription = builder.build();
            
            log.info("Creating Notification 2.0 subscription '{}' for device {} in plan {}", 
                    subscriptionName, deviceId, maintenancePlan.getId());
            
            notifications2Api.subscribe(subscription, notificationListener);

            // Create and return the subscription info
            DeviceSubscriptionInfo subscriptionInfo = DeviceSubscriptionInfo.builder()
                    .deviceId(deviceId)
                    .subscriptionName(subscriptionName)
                    .currentAccumulatedValue(0.0)
                    .createdTimestamp(new DateTime())
                    .active(true)
                    .build();

            log.info("Successfully created subscription for device {} in plan {}", 
                    deviceId, maintenancePlan.getId());
            
            return subscriptionInfo;

        } catch (Exception e) {
            log.error("Failed to create subscription for device {} in plan {}: {}", 
                    deviceId, maintenancePlan.getId(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Removes a Notification 2.0 subscription for a device.
     * 
     * @param maintenancePlan the maintenance plan
     * @param deviceId the device ID to unsubscribe
     * @return true if successfully removed, false otherwise
     * @since 1.0.0
     */
    public boolean removeSubscription(MaintenancePlan maintenancePlan, String deviceId) {
        String subscriptionName = MaintenancePlanMapper.generateSubscriptionName(maintenancePlan.getId());
        
        try {
            Subscription.ID subscriptionId = new Subscription.ID(subscriptionName, subscriberId);
            
            log.info("Removing Notification 2.0 subscription '{}' for device {} in plan {}", 
                    subscriptionName, deviceId, maintenancePlan.getId());
            
            notifications2Api.delete(subscriptionId);
            
            log.info("Successfully removed subscription for device {} in plan {}", 
                    deviceId, maintenancePlan.getId());
            
            return true;

        } catch (Exception e) {
            log.error("Failed to remove subscription for device {} in plan {}: {}", 
                    deviceId, maintenancePlan.getId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Disconnects a subscription without deleting it.
     * 
     * @param subscriptionName the subscription name
     * @param removeAllMessages whether to remove all pending messages
     * @since 1.0.0
     */
    public void disconnectSubscription(String subscriptionName, boolean removeAllMessages) {
        try {
            Subscription.ID subscriptionId = new Subscription.ID(subscriptionName, subscriberId);
            notifications2Api.disconnect(subscriptionId, removeAllMessages);
            log.info("Disconnected subscription '{}'", subscriptionName);
        } catch (Exception e) {
            log.error("Failed to disconnect subscription '{}': {}", subscriptionName, e.getMessage(), e);
        }
    }

    /**
     * Retrieves the active subscriptions map from a maintenance plan's managed object.
     * 
     * @param maintenancePlanId the maintenance plan ID
     * @return map of device ID to DeviceSubscriptionInfo
     * @since 1.0.0
     */
    public Map<String, DeviceSubscriptionInfo> getActiveSubscriptions(String maintenancePlanId) {
        try {
            ManagedObjectRepresentation mo = inventoryApi.get(GId.asGId(maintenancePlanId));
            MaintenancePlanMapper mapper = new MaintenancePlanMapper(mo);
            return mapper.getDeviceSubscriptions();
        } catch (Exception e) {
            log.error("Failed to get subscriptions for plan {}: {}", maintenancePlanId, e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Updates the device subscriptions fragment on the maintenance plan managed object.
     * 
     * @param maintenancePlanId the maintenance plan ID
     * @param subscriptions the updated subscriptions map
     * @since 1.0.0
     */
    public void updateSubscriptions(String maintenancePlanId, 
                                     Map<String, DeviceSubscriptionInfo> subscriptions) {
        try {
            MaintenancePlanMapper mapper = new MaintenancePlanMapper(maintenancePlanId);
            mapper.setDeviceSubscriptions(subscriptions);
            inventoryApi.update(mapper.getManagedObject());
            log.debug("Updated subscriptions for plan {}", maintenancePlanId);
        } catch (Exception e) {
            log.error("Failed to update subscriptions for plan {}: {}", 
                    maintenancePlanId, e.getMessage(), e);
        }
    }

    /**
     * Updates the accumulated value for a specific device subscription.
     * 
     * @param maintenancePlanId the maintenance plan ID
     * @param deviceId the device ID
     * @param valueToAdd the value to add to the accumulator
     * @return the DeviceSubscriptionInfo with updated accumulator, or null if not found
     * @since 1.0.0
     */
    public DeviceSubscriptionInfo updateAccumulator(String maintenancePlanId, 
                                                     String deviceId, 
                                                     Double valueToAdd) {
        Map<String, DeviceSubscriptionInfo> subscriptions = getActiveSubscriptions(maintenancePlanId);
        DeviceSubscriptionInfo info = subscriptions.get(deviceId);
        
        if (info == null) {
            log.warn("No subscription found for device {} in plan {}", deviceId, maintenancePlanId);
            return null;
        }

        info.addToAccumulator(valueToAdd);
        subscriptions.put(deviceId, info);
        updateSubscriptions(maintenancePlanId, subscriptions);
        
        log.debug("Updated accumulator for device {} in plan {} to {}", 
                deviceId, maintenancePlanId, info.getCurrentAccumulatedValue());
        
        return info;
    }

    /**
     * Resets the accumulator for a specific device subscription.
     * 
     * @param maintenancePlanId the maintenance plan ID
     * @param deviceId the device ID
     * @param cycleValue optional cycle value to reset to (null = 0)
     * @since 1.0.0
     */
    public void resetAccumulator(String maintenancePlanId, String deviceId, Integer cycleValue) {
        Map<String, DeviceSubscriptionInfo> subscriptions = getActiveSubscriptions(maintenancePlanId);
        DeviceSubscriptionInfo info = subscriptions.get(deviceId);
        
        if (info == null) {
            log.warn("No subscription found for device {} in plan {}", deviceId, maintenancePlanId);
            return;
        }

        if (cycleValue != null) {
            info.resetAccumulator(cycleValue.doubleValue());
        } else {
            info.resetAccumulator();
        }
        
        subscriptions.put(deviceId, info);
        updateSubscriptions(maintenancePlanId, subscriptions);
        
        log.info("Reset accumulator for device {} in plan {} to {}", 
                deviceId, maintenancePlanId, info.getCurrentAccumulatedValue());
    }

    /**
     * Disconnects all subscriptions for a given tenant.
     * Should be called when tenant unsubscribes from the microservice.
     * 
     * @param maintenancePlans list of maintenance plans to disconnect subscriptions for
     * @since 1.0.0
     */
    public void disconnectAllSubscriptions(List<MaintenancePlan> maintenancePlans) {
        for (MaintenancePlan plan : maintenancePlans) {
            if (plan.getOnUsage() != null) {
                String subscriptionName = MaintenancePlanMapper.generateSubscriptionName(plan.getId());
                disconnectSubscription(subscriptionName, true);
            }
        }
    }

    /**
     * Maps API string from subscription configuration to DeviceContextTargetApi enum.
     * 
     * @param api the API string (measurements, alarms, events)
     * @return the corresponding DeviceContextTargetApi
     * @throws IllegalArgumentException if API is not recognized
     * @since 1.0.0
     */
    private DeviceContextTargetApi mapApiToDeviceContextTargetApi(String api) {
        if (api == null) {
            throw new IllegalArgumentException("API cannot be null");
        }
        
        return switch (api.toLowerCase()) {
            case "measurements" -> DeviceContextTargetApi.MEASUREMENTS;
            case "alarms" -> DeviceContextTargetApi.ALARMS;
            case "events" -> DeviceContextTargetApi.EVENTS;
            default -> throw new IllegalArgumentException("Unknown API: " + api + 
                    ". Supported values: measurements, alarms, events");
        };
    }

    /**
     * Extracts the current tenant ID from context.
     * Note: This is a placeholder - actual implementation should use ContextService.
     * 
     * @return the current tenant ID
     * @since 1.0.0
     */
    private String extractTenantId() {
        // In production, this should be retrieved from ContextService<MicroserviceCredentials>
        // For now, we rely on the SDK handling this internally
        return "management";
    }
}
