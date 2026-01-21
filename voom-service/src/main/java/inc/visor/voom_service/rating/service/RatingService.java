package inc.visor.voom_service.rating.service;

import inc.visor.voom_service.rating.dto.RatingRequestDto;
import inc.visor.voom_service.rating.model.Rating;
import inc.visor.voom_service.rating.repository.RatingRepository;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RideRepository rideRepository;

    public void rateRide(Long rideId, RatingRequestDto dto) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found"));

        Rating rating = new Rating();
        rating.setRide(ride);
        rating.setDriverRating(dto.getDriverRating());
        rating.setVehicleRating(dto.getVehicleRating());
        rating.setComment(dto.getComment());

        ratingRepository.save(rating);
    }
}
