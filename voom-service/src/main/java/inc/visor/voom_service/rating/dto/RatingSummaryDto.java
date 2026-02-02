package inc.visor.voom_service.rating.dto;

import inc.visor.voom_service.auth.user.dto.UserProfileDto;
import inc.visor.voom_service.rating.model.Rating;

import java.time.LocalDateTime;

public class RatingSummaryDto {
    String message;
    int driverRating;
    int vehicleRating;
    LocalDateTime createdAt;
    UserProfileDto rater;

    public RatingSummaryDto(Rating rating) {
        this.message = rating.getComment();
        this.driverRating = rating.getDriverRating();
        this.vehicleRating = rating.getVehicleRating();
        this.createdAt = rating.getCreatedAt();
        this.rater = new UserProfileDto(rating.getRater());
    }
}
