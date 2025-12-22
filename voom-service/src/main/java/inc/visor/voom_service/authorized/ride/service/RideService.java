package inc.visor.voom_service.authorized.ride.service;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.authorized.ride.dto.CreateRideRequestDto;
import inc.visor.voom_service.authorized.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.domain.ride.enums.RideRequestStatus;

@Service
public class RideService {

    public RideRequestResponseDto createRideRequest(
            User user,
            CreateRideRequestDto request
    ) {
        double mockPrice = 1500; 

        return new RideRequestResponseDto(
                1L,
                RideRequestStatus.ACCEPTED,
                mockPrice
        );
    }


    public RideRequestResponseDto createFromFavorite(
            User user,
            Long favoriteRouteId,
            Object request
    ) {
        double mockPrice = 1200; 

        return new RideRequestResponseDto(
                2L,
                RideRequestStatus.PENDING,
                mockPrice
        );
    }
}
