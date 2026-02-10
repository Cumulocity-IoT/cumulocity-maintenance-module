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

@Component
public class MaintenanceScheduler {
	private static final Logger log = LoggerFactory.getLogger(MaintenanceScheduler.class);

	private final TimeBasedMaintenanceService timeBasedMaintenanceService;

    private final SubscriptionBasedMaintenanceService subscriptionBasedMaintenanceService;
	
	private final TaskScheduler taskScheduler;

    @Value("${scheduled.timebased.run.rate.millis:60000}")
    protected Long timeBasedMaintenanceRate;

    @Value("${scheduled.subscription.run.rate.millis:3600000}")
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
     * Cancel tenant separate scheduled task.
     *
     * @param event
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
            //TODO remove all notification subscriptions for this tenant!!!
        }
    }
}
