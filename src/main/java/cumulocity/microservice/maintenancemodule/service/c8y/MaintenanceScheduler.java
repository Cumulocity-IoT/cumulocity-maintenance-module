package cumulocity.microservice.maintenancemodule.service.c8y;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionRemovedEvent;

import jakarta.annotation.PreDestroy;

@Component
public class MaintenanceScheduler {
	private static final Logger log = LoggerFactory.getLogger(MaintenanceScheduler.class);

	private final TimeBasedMaintenanceService timeBasedMaintenanceService;

    private final SubscriptionBasedMaintenanceService subscriptionBasedMaintenanceService;
	
	private final TaskScheduler taskScheduler;

    @Value("${scheduled.timebased.enabled:false}")
    protected Boolean isTimeBasedSchedulerEnabled;

    @Value("${scheduled.timebased.run.rate.millis:60000}")
    protected Long timeBasedMaintenanceRate;

    @Value("${scheduled.subscriptionbased.enabled:true}")
    protected Boolean isSubscriptionBasedSchedulerEnabled;

    @Value("${scheduled.subscription.run.rate.millis:60000}")
    protected Long subscriptionBasedMaintenanceRate;
	
    private Map<String, ScheduledFuture<?>> timeBasedScheduledMap = new HashMap<>();

    private Map<String, ScheduledFuture<?>> subscriptionBasedScheduledMap = new HashMap<>();
    
	public MaintenanceScheduler(TimeBasedMaintenanceService timeBasedMaintenanceService, SubscriptionBasedMaintenanceService subscriptionBasedMaintenanceService, TaskScheduler taskScheduler) {
		this.timeBasedMaintenanceService = timeBasedMaintenanceService;
        this.subscriptionBasedMaintenanceService = subscriptionBasedMaintenanceService;
		this.taskScheduler = taskScheduler;
	}
	
    /**
     * Register for each tenant a separate scheduled task.
     *
     * @param event
     */
    @EventListener
    private void onSubscriptionAdded(final MicroserviceSubscriptionAddedEvent event) {
        MicroserviceCredentials credentials = event.getCredentials();
        
        if(isTimeBasedSchedulerEnabled) {
        	scheduleTimeBasedMaintenance(credentials);
        }

        if(isSubscriptionBasedSchedulerEnabled) {
            subscriptionBasedMaintenanceService.initializeSubscriptionsForExistingPlans(credentials);
        	scheduleSubscriptionBasedMaintenance(credentials);
        }

    }

    private void scheduleTimeBasedMaintenance(MicroserviceCredentials credentials) {
        log.info("Creating time based Scheduler task for tenant: {}", credentials.getTenant());
        ScheduledFuture<?> timeBasedMaintenanceScheduledTask = taskScheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				String tenant = getCredentials().getTenant();
				
				log.info("START, scheduled task for tenant: {}", tenant);
				timeBasedMaintenanceService.runJobWithinContext(getCredentials());
				log.info("END, scheduled task for tenant: {}", tenant);
			}
			
			private MicroserviceCredentials getCredentials() {
				return credentials;
			}
			
		}, Duration.ofMillis(timeBasedMaintenanceRate));
        timeBasedScheduledMap.put(credentials.getTenant(), timeBasedMaintenanceScheduledTask);
    }

    private void scheduleSubscriptionBasedMaintenance(MicroserviceCredentials credentials) {
        log.info("Creating subscription based Scheduler task for tenant: {}", credentials.getTenant());
        ScheduledFuture<?> subscriptionBasedMaintenanceScheduledTask = taskScheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				String tenant = getCredentials().getTenant();
				
				log.info("START, scheduled task for tenant: {}", tenant);
				subscriptionBasedMaintenanceService.runJobWithinContext(getCredentials());
				log.info("END, scheduled task for tenant: {}", tenant);
			}
			
			private MicroserviceCredentials getCredentials() {
				return credentials;
			}
			
		}, Duration.ofMillis(subscriptionBasedMaintenanceRate));
        subscriptionBasedScheduledMap.put(credentials.getTenant(), subscriptionBasedMaintenanceScheduledTask);
    }

    /**
     * Cancel tenant separate scheduled task and cleanup notification subscriptions.
     *
     * @param event the subscription removed event
     * @since 1.0.0
     */
    @EventListener
    private void onSubscriptionRemoved(final MicroserviceSubscriptionRemovedEvent event) {
        log.info("Cancel time based Scheduler task for tenant: {}", event.getTenant());
        
    	ScheduledFuture<?> scheduledFuture = timeBasedScheduledMap.get(event.getTenant());
    	if(scheduledFuture != null) {
    		scheduledFuture.cancel(true);
            timeBasedScheduledMap.remove(event.getTenant());
    	}

        log.info("Cancel subscription based Scheduler task for tenant: {}", event.getTenant());
        ScheduledFuture<?> subscriptionBasedScheduledFuture = subscriptionBasedScheduledMap.get(event.getTenant());
        if(subscriptionBasedScheduledFuture != null) {
            subscriptionBasedScheduledFuture.cancel(true);
            subscriptionBasedScheduledMap.remove(event.getTenant());
            
            // Disconnect all Notification 2.0 subscriptions for this tenant
            log.info("Disconnecting all notification subscriptions for tenant: {}", event.getTenant());
            MicroserviceCredentials credentials = MicroserviceCredentials.builder()
                    .tenant(event.getTenant())
                    .build();
            subscriptionBasedMaintenanceService.disconnectAllSubscriptionsForTenant(credentials);
        }
    }

    /**
     * Cleanup method called during graceful shutdown.
     * Cancels all running scheduled tasks and disconnects notification subscriptions.
     * 
     * @since 1.0.0
     */
    @PreDestroy
    public void onShutdown() {
        log.info("Graceful shutdown initiated - cleaning up scheduled tasks");
        
        // Cancel all time-based scheduled tasks
        timeBasedScheduledMap.forEach((tenant, scheduledFuture) -> {
            try {
                log.info("Cancelling time-based scheduled task for tenant: {}", tenant);
                scheduledFuture.cancel(false); // Don't interrupt if running
            } catch (Exception e) {
                log.error("Error cancelling time-based task for tenant {}: {}", tenant, e.getMessage(), e);
            }
        });
        timeBasedScheduledMap.clear();
        
        // Cancel all subscription-based scheduled tasks and disconnect subscriptions
        subscriptionBasedScheduledMap.forEach((tenant, scheduledFuture) -> {
            try {
                log.info("Cancelling subscription-based scheduled task for tenant: {}", tenant);
                scheduledFuture.cancel(false); // Don't interrupt if running
                
                // Disconnect all notification subscriptions for this tenant
                log.info("Disconnecting notification subscriptions for tenant: {}", tenant);
                MicroserviceCredentials credentials = MicroserviceCredentials.builder()
                        .tenant(tenant)
                        .build();
                subscriptionBasedMaintenanceService.disconnectAllSubscriptionsForTenant(credentials);
            } catch (Exception e) {
                log.error("Error during cleanup for tenant {}: {}", tenant, e.getMessage(), e);
            }
        });
        subscriptionBasedScheduledMap.clear();
        
        log.info("Graceful shutdown cleanup completed");
    }
}
