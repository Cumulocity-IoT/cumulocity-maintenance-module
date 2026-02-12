package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import cumulocity.microservice.maintenancemodule.model.DeviceSubscriptionInfo;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing subscription-based (usage-based) maintenance.
 * Reconciles Notification 2.0 subscriptions with applied devices for each maintenance plan.
 * 
 * @author APES
 * @since 1.0.0
 */
@Slf4j
@Service
public class SubscriptionBasedMaintenanceService {
    private final ContextService<MicroserviceCredentials> contextService;

    private final MaintenancePlanService maintenancePlanService;

    private final MaintenancePlanApplyService maintenancePlanApplyService;

    private final UsageSubscriptionService usageSubscriptionService;

    private final InventoryApi inventoryApi;

    private final AlarmApi alarmApi;

    /**
     * Constructs the SubscriptionBasedMaintenanceService with required dependencies.
     * 
     * @param contextService the context service for tenant-aware operations
     * @param maintenancePlanService service for managing maintenance plans
     * @param maintenancePlanApplyService service for resolving applied devices
     * @param usageSubscriptionService service for managing Notification 2.0 subscriptions
     * @param inventoryApi Cumulocity Inventory API
     * @param alarmApi Cumulocity Alarm API
     * @since 1.0.0
     */
    public SubscriptionBasedMaintenanceService(ContextService<MicroserviceCredentials> contextService, 
                                                MaintenancePlanService maintenancePlanService, 
                                                MaintenancePlanApplyService maintenancePlanApplyService, 
                                                UsageSubscriptionService usageSubscriptionService,
                                                InventoryApi inventoryApi, 
                                                AlarmApi alarmApi) {
        this.contextService = contextService;
        this.maintenancePlanService = maintenancePlanService;
        this.maintenancePlanApplyService = maintenancePlanApplyService;
        this.usageSubscriptionService = usageSubscriptionService;
        this.inventoryApi = inventoryApi;
        this.alarmApi = alarmApi;
    }

    /**
     * Runs the subscription-based maintenance job within the given tenant context.
     * Reconciles subscriptions by adding new device subscriptions and removing stale ones.
     * 
     * @param context the microservice credentials for the tenant
     * @return true if job completed successfully, false otherwise
     * @since 1.0.0
     */
    public Boolean runJobWithinContext(MicroserviceCredentials context) {
        Boolean callWithinContext = contextService.callWithinContext(context, (Callable<Boolean>) () -> {
            try {
                log.info("Start usage-based maintenance job for tenant {}", context.getTenant());
                List<MaintenancePlan> maintenancePlans = maintenancePlanService.getAllMaintenancePlansByType(MaintenancePlanMapper.MP_ON_USAGE);
                log.info("Found {} usage-based maintenance plans", maintenancePlans.size());
                
                for (MaintenancePlan maintenancePlan : maintenancePlans) {
                    MaintenancePlan maintenancePlanUpdated = checkAndUpdateActivateFlag(maintenancePlan);
                    if(maintenancePlanUpdated.getActive() == null || !maintenancePlanUpdated.getActive()) {
                        log.info("Skipping inactive maintenance plan {}", maintenancePlan.getName());
                        log.debug("Reconcile subscriptions for deactivated maintenance plan {}", maintenancePlan.getName());
                        reconcileDeactivatedPlanSubscriptions(maintenancePlanUpdated);
                        continue;
                    }
                    
                    reconcileActivePlanSubscriptions(maintenancePlanUpdated);
                }

                //TODO we need also to take care if active maintenance plans get removed. A clean up of the subscriptions is necessary for not existing plans!
                
                log.info("Usage-based maintenance subscription reconciliation completed");
                return Boolean.TRUE;
            } catch (Exception e) {
                log.error("Maintenance job for tenant {} failed with message {}", context.getTenant(), e.getMessage(), e);
            }
            return Boolean.FALSE;
        });
        return callWithinContext;
    }

    public void initializeSubscriptionsForExistingPlans(MicroserviceCredentials context) {
        Boolean callWithinContext = contextService.callWithinContext(context, (Callable<Boolean>) () -> {
            try {
                log.info("Initializing subscriptions for existing maintenance plans for tenant {}", context.getTenant());
                List<MaintenancePlan> maintenancePlans = maintenancePlanService.getAllMaintenancePlansByType(MaintenancePlanMapper.MP_ON_USAGE);
                for (MaintenancePlan maintenancePlan : maintenancePlans) {
                    if(Boolean.TRUE.equals(maintenancePlan.getActive())) {
                        log.info("Initializing subscriptions for active maintenance plan {}", maintenancePlan.getName());
                        initActivePlanSubscriptions(maintenancePlan);
                    }
                }
                log.info("Initialization of subscriptions for existing plans completed for tenant {}", context.getTenant());
            } catch (Exception e) {
                log.error("Failed to initialize subscriptions for existing plans for tenant {}: {}", 
                        context.getTenant(), e.getMessage(), e);
            }
            return Boolean.TRUE;
        });
    }

    private void initActivePlanSubscriptions(MaintenancePlan maintenancePlan) {
        log.info("Reconciling subscriptions for maintenance plan: {}", maintenancePlan.getName());
        
        // Get currently applied devices
        Set<ManagedObjectRepresentation> appliedDevices = maintenancePlanApplyService.getAllAppliedDevices(maintenancePlan);
        Set<String> appliedDeviceIds = appliedDevices.stream()
                .map(device -> device.getId().getValue())
                .collect(Collectors.toSet());
        
        log.info("Maintenance plan {} applies to {} devices", maintenancePlan.getName(), appliedDeviceIds.size());
        
        // Get currently subscribed devices
        Map<String, DeviceSubscriptionInfo> currentSubscriptions = 
                usageSubscriptionService.getActiveSubscriptions(maintenancePlan.getId());
        Set<String> subscribedDeviceIds = new HashSet<>(currentSubscriptions.keySet());
        
        log.info("Maintenance plan {} has {} active subscriptions", maintenancePlan.getName(), subscribedDeviceIds.size());
        
        // Find devices to subscribe (in applied but not in subscribed)
        Set<String> devicesToSubscribe = new HashSet<>(appliedDeviceIds);
        
        log.info("Plan {}: {} devices to subscribe", 
                maintenancePlan.getName(), devicesToSubscribe.size());
        
        // Create map to track updates
        Map<String, DeviceSubscriptionInfo> updatedSubscriptions = new HashMap<>(currentSubscriptions);
        
        // Subscribe new devices
        for (String deviceId : devicesToSubscribe) {
            ManagedObjectRepresentation device = appliedDevices.stream()
                    .filter(d -> d.getId().getValue().equals(deviceId))
                    .findFirst()
                    .orElse(null);
            
            if (device != null) {
                DeviceSubscriptionInfo subscriptionInfo = usageSubscriptionService.createSubscription(
                        maintenancePlan, device);
                if (subscriptionInfo != null) {
                    updatedSubscriptions.put(deviceId, subscriptionInfo);
                    log.info("Created subscription for device {} in plan {}", deviceId, maintenancePlan.getName());
                }
            }
        }
        
        // Update the subscriptions in the managed object if there were changes
        if (!devicesToSubscribe.isEmpty()) {
            usageSubscriptionService.updateSubscriptions(maintenancePlan.getId(), updatedSubscriptions);
            log.info("Updated subscriptions for plan {} - total active: {}", 
                    maintenancePlan.getName(), updatedSubscriptions.size());
        }
    }

    /**
     * Reconciles subscriptions for active maintenance plan.
     * Adds subscriptions for newly applied devices and removes subscriptions for devices no longer applied.
     * 
     * @param maintenancePlan the maintenance plan to reconcile subscriptions for
     * @since 1.0.0
     */
    private void reconcileActivePlanSubscriptions(MaintenancePlan maintenancePlan) {
        log.info("Reconciling subscriptions for maintenance plan: {}", maintenancePlan.getName());
        
        // Get currently applied devices
        Set<ManagedObjectRepresentation> appliedDevices = maintenancePlanApplyService.getAllAppliedDevices(maintenancePlan);
        Set<String> appliedDeviceIds = appliedDevices.stream()
                .map(device -> device.getId().getValue())
                .collect(Collectors.toSet());
        
        log.info("Maintenance plan {} applies to {} devices", maintenancePlan.getName(), appliedDeviceIds.size());
        
        // Get currently subscribed devices
        Map<String, DeviceSubscriptionInfo> currentSubscriptions = 
                usageSubscriptionService.getActiveSubscriptions(maintenancePlan.getId());
        Set<String> subscribedDeviceIds = new HashSet<>(currentSubscriptions.keySet());
        
        log.info("Maintenance plan {} has {} active subscriptions", maintenancePlan.getName(), subscribedDeviceIds.size());
        
        // Find devices to subscribe (in applied but not in subscribed)
        Set<String> devicesToSubscribe = new HashSet<>(appliedDeviceIds);
        devicesToSubscribe.removeAll(subscribedDeviceIds);
        
        // Find devices to unsubscribe (in subscribed but not in applied)
        Set<String> devicesToUnsubscribe = new HashSet<>(subscribedDeviceIds);
        devicesToUnsubscribe.removeAll(appliedDeviceIds);
        
        log.info("Plan {}: {} devices to subscribe, {} devices to unsubscribe", 
                maintenancePlan.getName(), devicesToSubscribe.size(), devicesToUnsubscribe.size());
        
        // Create map to track updates
        Map<String, DeviceSubscriptionInfo> updatedSubscriptions = new HashMap<>(currentSubscriptions);
        
        // Subscribe new devices
        for (String deviceId : devicesToSubscribe) {
            ManagedObjectRepresentation device = appliedDevices.stream()
                    .filter(d -> d.getId().getValue().equals(deviceId))
                    .findFirst()
                    .orElse(null);
            
            if (device != null) {
                DeviceSubscriptionInfo subscriptionInfo = usageSubscriptionService.createSubscription(
                        maintenancePlan, device);
                if (subscriptionInfo != null) {
                    updatedSubscriptions.put(deviceId, subscriptionInfo);
                    log.info("Created subscription for device {} in plan {}", deviceId, maintenancePlan.getName());
                }
            }
        }
        
        // Unsubscribe removed devices
        for (String deviceId : devicesToUnsubscribe) {
            boolean removed = usageSubscriptionService.removeSubscription(maintenancePlan, deviceId);
            if (removed) {
                updatedSubscriptions.remove(deviceId);
                log.info("Removed subscription for device {} from plan {}", deviceId, maintenancePlan.getName());
            }
        }
        
        // Update the subscriptions in the managed object if there were changes
        if (!devicesToSubscribe.isEmpty() || !devicesToUnsubscribe.isEmpty()) {
            usageSubscriptionService.updateSubscriptions(maintenancePlan.getId(), updatedSubscriptions);
            log.info("Updated subscriptions for plan {} - total active: {}", 
                    maintenancePlan.getName(), updatedSubscriptions.size());
        }
    }

    private void reconcileDeactivatedPlanSubscriptions(MaintenancePlan maintenancePlan) {
        log.info("Deactivating subscriptions for maintenance plan: {}", maintenancePlan.getName());
        
        // Get currently subscribed devices
        Map<String, DeviceSubscriptionInfo> currentSubscriptions = 
                usageSubscriptionService.getActiveSubscriptions(maintenancePlan.getId());
        Map<String, DeviceSubscriptionInfo> updatedSubscriptions = new HashMap<>(currentSubscriptions);
        
        // Unsubscribe all devices
        for (String deviceId : currentSubscriptions.keySet()) {
            boolean removed = usageSubscriptionService.removeSubscription(maintenancePlan, deviceId);
            if (removed) {
                log.info("Removed subscription for device {} from deactivated plan {}", deviceId, maintenancePlan.getName());
                updatedSubscriptions.remove(deviceId);
            }
        }

        usageSubscriptionService.updateSubscriptions(maintenancePlan.getId(), updatedSubscriptions);
    }

    /**
     * Checks and updates the active flag of a maintenance plan based on start/end dates.
     * 
     * @param maintenancePlan the maintenance plan to check
     * @return the updated maintenance plan
     * @since 1.0.0
     */
    private MaintenancePlan checkAndUpdateActivateFlag(MaintenancePlan maintenancePlan) {
        if(Boolean.FALSE.equals(maintenancePlan.getActive())) {
            log.info("Maintenance plan {} is already inactive, skipping date check", maintenancePlan.getName());
            return maintenancePlan;
        }
        // If plan is active, we check if it should be deactivated based on dates
        DateTime startDate = maintenancePlan.getStartDate();
        DateTime endDate = maintenancePlan.getEndDate();
        Boolean currentActivate = maintenancePlan.getActive();
        if(startDate == null && endDate == null) {
            return maintenancePlan;
        }

        DateTime currentDate = new DateTime();
        Boolean newActive = startDate != null && currentDate.isAfter(startDate) || endDate != null && currentDate.isBefore(endDate);
        if(currentActivate == null || !currentActivate.equals(newActive)) {
            log.info("Updating maintenance plan {} activate flag from {} to {}", maintenancePlan.getName(), currentActivate, newActive);
            return maintenancePlanService.updatActivateFlag(maintenancePlan.getId(), newActive);
        }

        return maintenancePlan;
    }

    /**
     * Disconnects all subscriptions for a tenant.
     * Should be called when tenant unsubscribes from the microservice.
     * 
     * @param context the microservice credentials for the tenant
     * @since 1.0.0
     */
    public void disconnectAllSubscriptionsForTenant(MicroserviceCredentials context) {
        contextService.callWithinContext(context, (Callable<Void>) () -> {
            try {
                log.info("Disconnecting all subscriptions for tenant {}", context.getTenant());
                List<MaintenancePlan> maintenancePlans = maintenancePlanService.getAllMaintenancePlansByType(MaintenancePlanMapper.MP_ON_USAGE);
                usageSubscriptionService.disconnectAllSubscriptions(maintenancePlans);
                log.info("Disconnected all subscriptions for tenant {}", context.getTenant());
            } catch (Exception e) {
                log.error("Failed to disconnect subscriptions for tenant {}: {}", 
                        context.getTenant(), e.getMessage(), e);
            }
            return null;
        });
    }
}
