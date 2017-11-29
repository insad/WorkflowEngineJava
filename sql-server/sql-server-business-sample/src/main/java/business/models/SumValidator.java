package business.models;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class SumValidator implements ConstraintValidator<ValidSum, BigDecimal> {
    @Override
    public void initialize(ValidSum constraintAnnotation) {
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value == null || value.compareTo(BigDecimal.ZERO) >= 0;
    }
}
