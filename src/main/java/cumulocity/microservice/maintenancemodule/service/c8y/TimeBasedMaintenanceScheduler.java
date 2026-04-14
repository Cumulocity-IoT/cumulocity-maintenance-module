package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.time.Duration;

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
public class TimeBasedMaintenanceScheduler {
	private static final Logger log = LoggerFactory.getLogger(TimeBasedMaintenanceScheduler.class);

	private final TimeBasedMaintenanceService timeBasedMaintenanceService;
	
	private final TaskScheduler taskScheduler;

    @Value("${scheduled.run.rate.millis:60000}")
    protected Long runRateMillis;
	
    private Map<String, ScheduledFuture<?>> scheduledFutureMap = new HashMap<>();
    
	public TimeBasedMaintenanceScheduler(TimeBasedMaintenanceService timeBasedMaintenanceService, TaskScheduler taskScheduler) {
		this.timeBasedMaintenanceService = timeBasedMaintenanceService;
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
        String tenant = credentials.getTenant();

        log.info("Creating Scheduler task for tenant: {}", tenant);

        ScheduledFuture<?> scheduleWithFixedDelay = taskScheduler.scheduleWithFixedDelay(() -> {
            log.info("START, scheduled task for tenant: {}", tenant);
            timeBasedMaintenanceService.runJobWithinContext(credentials);
            log.info("END, scheduled task for tenant: {}", tenant);
        }, Duration.ofMillis(runRateMillis));

        scheduledFutureMap.put(tenant, scheduleWithFixedDelay);
    }
    
    /**
     * Cancel tenant separate scheduled task.
     *
     * @param event
     */
    @EventListener
    private void onSubscriptionRemoved(final MicroserviceSubscriptionRemovedEvent event) {
        log.info("Cancel Scheduler task for tenant: {}", event.getTenant());
        
    	ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(event.getTenant());
    	if(scheduledFuture != null) {
    		scheduledFuture.cancel(true);
    	}
    }
}
