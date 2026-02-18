package inc.visor.voom_service.chat.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailOrAdminValidator implements ConstraintValidator<ValidEmailOrAdmin, String> {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        if ("admin".equalsIgnoreCase(value)) {
            return true;
        }

        return Pattern.compile(EMAIL_PATTERN)
                .matcher(value)
                .matches();
    }
}
