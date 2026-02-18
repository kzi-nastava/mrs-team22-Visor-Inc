package inc.visor.voom_service.chat.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailOrAdminValidator.class)
@Documented
public @interface ValidEmailOrAdmin {
    String message() default "Must be a valid email format or 'admin'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
