package bg.softuni.serviceScheduler.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MileageEqualOrGreaterValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MileageEqualOrGreater {

    String message() default "New value must be greater than old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
