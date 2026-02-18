package inc.visor.voom_service.driver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator
        implements ConstraintValidator<PasswordMatch, PasswordConfirmable> {

    @Override
    public boolean isValid(
            PasswordConfirmable dto,
            ConstraintValidatorContext context
    ) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return true;
        }

        return dto.getPassword().equals(dto.getConfirmPassword());
    }
}
