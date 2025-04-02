package bg.softuni.serviceScheduler.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueVinValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueVin {

    String message() default "Vehicle with the same VIN number already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
