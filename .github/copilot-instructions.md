---
applyTo: "*.java"
---

# Copilot Instructions for Java Development

## Documentation Requirements

### Class Documentation
- Every class must have a JavaDoc comment that includes:
  - Brief description of the class purpose and functionality
  - `@author` tag with the developer's name
  - `@since` tag indicating the version when the class was introduced
  - `@version` tag if applicable

### Method Documentation
- Every public method must have JavaDoc documentation that includes:
  - Clear description of what the method does
  - `@param` tags for each parameter with description
  - `@return` tag describing the return value (if not void)
  - `@throws` or `@exception` tags for any checked exceptions
  - `@since` tag if the method was added after initial class creation

### JavaDoc Best Practices
- Use complete sentences in descriptions
- Start descriptions with a verb in third person (e.g., "Creates", "Returns", "Validates")
- Include examples using `@code` or `@link` tags when helpful
- Use `@deprecated` tag for deprecated methods with migration guidance
- Keep descriptions concise but comprehensive

### Example Format
```java
/**
 * Manages maintenance schedules and operations for Cumulocity devices.
 * Provides functionality to create, update, and track maintenance tasks.
 * 
 * @author Your Name
 * @since 1.0.0
 * @version 1.2.0
 */
public class MaintenanceManager {
    
    /**
     * Creates a new maintenance schedule for the specified device.
     * 
     * @param deviceId the unique identifier of the device
     * @param schedule the maintenance schedule configuration
     * @return the created maintenance schedule with assigned ID
     * @throws IllegalArgumentException if deviceId is null or empty
     * @throws MaintenanceException if schedule creation fails
     * @since 1.0.0
     */
    public MaintenanceSchedule createSchedule(String deviceId, ScheduleConfig schedule) {
        // implementation
    }
}
```

## Code Quality Standards
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Implement proper error handling with appropriate exceptions
- Include unit tests for public methods when generating test classes
- Use Slf4j for logging in each class

## Special Cumulocity Microservice Requirements
- REST PATCH is not allowed in Cumulocity microservices
- The microservice name must follow the this regex: `^[a-z0-9-]{1,23}$`
