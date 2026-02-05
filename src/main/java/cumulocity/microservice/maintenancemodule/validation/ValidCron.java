package cumulocity.microservice.maintenancemodule.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a string is a valid cron expression.
 * Uses Spring's CronExpression parser for validation.
 * 
 * @author APES
 * @since 1.1.0
 */
@Documented
@Constraint(validatedBy = ValidCronValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCron {
    
    /**
     * Returns the error message when validation fails.
     * 
     * @return the error message
     */
    String message() default "Invalid cron expression";
    
    /**
     * Returns the validation groups.
     * 
     * @return the validation groups
     */
    Class<?>[] groups() default {};
    
    /**
     * Returns the payload.
     * 
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};
}
