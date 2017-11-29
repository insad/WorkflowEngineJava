package business.models;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = SumValidator.class)
public @interface ValidSum {
    String message() default "Sum must be positive or zero value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}