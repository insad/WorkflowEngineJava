package business.models;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class SumValidator implements ConstraintValidator<ValidSum, String> {
    @Override
    public void initialize(ValidSum constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String s = value.replace(',', '.');
        try {
            BigDecimal sum = new BigDecimal(s);
            return sum.compareTo(BigDecimal.ZERO) >= 0;
        } catch (Exception ignored) {
            return false;
        }
    }
}
