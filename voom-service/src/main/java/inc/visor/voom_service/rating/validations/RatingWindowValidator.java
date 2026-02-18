package inc.visor.voom_service.rating.validations;

import inc.visor.voom_service.rating.model.Rating;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class RatingWindowValidator implements ConstraintValidator<ValidRatingTime, Rating> {

    @Override
    public boolean isValid(Rating rating, ConstraintValidatorContext context) {
        if (rating == null || rating.getRide() == null) {
            return true;
        }

        if (rating.getId() != null) {
            return true;
        }

        if (rating.getRide().getFinishedAt() == null) {
            return false;
        }

        LocalDateTime rideTime = rating.getRide().getFinishedAt();
        LocalDateTime ratingTime = LocalDateTime.now();

        return ratingTime.isBefore(rideTime.plusDays(3));
    }
}
