package cumulocity.microservice.maintenancemodule;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
// NEW IMPORT for Spring Boot 3.4+
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class AppTest {

    // REPLACED @MockBean with @MockitoBean
    @MockitoBean
    private MicroserviceSubscriptionsService subscriptionsService;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }
}