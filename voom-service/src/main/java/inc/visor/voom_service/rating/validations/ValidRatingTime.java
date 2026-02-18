package inc.visor.voom_service.rating.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RatingWindowValidator.class)
public @interface ValidRatingTime {
    String message() default "Ratings must be submited within 3 days of finishing the ride";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
