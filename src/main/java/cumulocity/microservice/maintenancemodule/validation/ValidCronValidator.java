package cumulocity.microservice.maintenancemodule.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronExpression;

/**
 * Validator implementation for {@link ValidCron} annotation.
 * Validates cron expressions using Spring's CronExpression parser.
 * 
 * @author APES
 * @since 1.1.0
 */
public class ValidCronValidator implements ConstraintValidator<ValidCron, String> {
    
    private static final Logger log = LoggerFactory.getLogger(ValidCronValidator.class);
    
    /**
     * Initializes the validator.
     * 
     * @param constraintAnnotation annotation instance
     */
    @Override
    public void initialize(ValidCron constraintAnnotation) {
        // No initialization needed
    }
    
    /**
     * Validates the cron expression.
     * 
     * @param value the cron expression to validate
     * @param context context in which the constraint is evaluated
     * @return true if the cron expression is valid, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        try {
            CronExpression.parse(value);
            return true;
        } catch (IllegalArgumentException e) {
            log.debug("Invalid cron expression: {}", value, e);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Invalid cron expression: " + e.getMessage()
            ).addConstraintViolation();
            return false;
        }
    }
}
