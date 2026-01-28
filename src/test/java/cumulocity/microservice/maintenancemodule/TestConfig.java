package cumulocity.microservice.maintenancemodule;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.sdk.client.Platform;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public MicroserviceSubscriptionsService microserviceSubscriptionsService() {
        return mock(MicroserviceSubscriptionsService.class);
    }

    @Bean
    @Primary
    public Platform platform() {
        // Just return the mock.
        // We cannot stub getCumulocityCredentials() because the method
        // no longer exists in the Platform interface.
        return mock(Platform.class);
    }
}