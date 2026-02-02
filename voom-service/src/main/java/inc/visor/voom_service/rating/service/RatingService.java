package inc.visor.voom_service.rating.service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.rating.dto.RatingRequestDto;
import inc.visor.voom_service.rating.model.Rating;
import inc.visor.voom_service.rating.repository.RatingRepository;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.repository.RideRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RideRepository rideRepository;

    @Transactional
    public void rateRide(User user, Long rideId, RatingRequestDto dto) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found"));

        Rating rating = new Rating();
        rating.setRide(ride);
        rating.setDriverRating(dto.getDriverRating());
        rating.setVehicleRating(dto.getVehicleRating());
        rating.setComment(dto.getComment());
        rating.setRater(user);

        ratingRepository.save(rating);
    }

    public List<Rating> getByRide(Long rideId) {
        return ratingRepository.findByRideId(rideId);
    }

    public List<Rating> getByDriver(Long userId) {
        return ratingRepository.findByRide_Driver_Id(userId);
    }
}
