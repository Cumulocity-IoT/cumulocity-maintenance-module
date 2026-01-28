package cumulocity.microservice.maintenancemodule;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class AppTest {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }
}