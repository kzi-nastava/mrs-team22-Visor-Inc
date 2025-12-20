package inc.visor.voom_service.auth.driver.validation;

import inc.visor.voom_service.auth.driver.dto.ActivateDriverRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator
        implements ConstraintValidator<PasswordMatch, ActivateDriverRequestDto> {

    @Override
    public boolean isValid(
            ActivateDriverRequestDto dto,
            ConstraintValidatorContext context
    ) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return true; 
        }

        return dto.getPassword().equals(dto.getConfirmPassword());
    }
}
