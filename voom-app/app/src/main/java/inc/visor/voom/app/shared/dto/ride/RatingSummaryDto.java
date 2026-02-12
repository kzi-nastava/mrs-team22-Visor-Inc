package inc.visor.voom.app.shared.dto.ride;

import java.time.LocalDateTime;

import inc.visor.voom.app.admin.users.dto.UserProfileDto;

public class RatingSummaryDto {
    String message;
    int driverRating;
    int vehicleRating;
    String createdAt;
    UserProfileDto rater;

    public RatingSummaryDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public int getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(int vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserProfileDto getRater() {
        return rater;
    }

    public void setRater(UserProfileDto rater) {
        this.rater = rater;
    }
}
