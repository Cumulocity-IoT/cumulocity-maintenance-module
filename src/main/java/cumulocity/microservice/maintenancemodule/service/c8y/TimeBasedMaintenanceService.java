package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;

@Service
public class TimeBasedMaintenanceService {
    private static final Logger log = LoggerFactory.getLogger(TimeBasedMaintenanceService.class);

    private final ContextService<MicroserviceCredentials> contextService;

    @Autowired
    public TimeBasedMaintenanceService(ContextService<MicroserviceCredentials> contextService) {
        this.contextService = contextService;
    }

    public Boolean runJobWithinContext(MicroserviceCredentials context) {
        Boolean callWithinContext = contextService.callWithinContext(context, (Callable<Boolean>) () -> {
            try {
                log.info("Start time-based maintenance job for tenant {}", context.getTenant());
                //TODO do something useful here
                log.info("time-based maintenance devices processed");
                return Boolean.TRUE;
            } catch (Exception e) {
                log.error("Maintenance job for tenant {} failed with message {}", context.getTenant(), e.getMessage(), e);
            }
            return Boolean.FALSE;
        });
        return callWithinContext;
    }
}
