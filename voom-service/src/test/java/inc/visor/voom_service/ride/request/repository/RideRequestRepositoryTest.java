package inc.visor.voom_service.ride.request.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import inc.visor.voom_service.ride.repository.RideRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

@DataJpaTest
@ActiveProfiles("test")
class RideRequestRepositoryTest {

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("1 - Should return scheduled rides inside time range")
    void shouldReturnScheduledRidesWithinRange() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(15);

        User creator = getExistingUser();

        createValidRideRequest(creator);

        List<RideRequest> result
                = rideRequestRepository.findUpcomingScheduled(now, threshold);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("2 - Should not return rides with scheduleType NOW")
    void shouldNotReturnNowRequests() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(15);

        User creator = getExistingUser();

        RideRequest request = createValidRideRequest(creator);
        request.setScheduleType(ScheduleType.NOW);
        entityManager.flush();

        List<RideRequest> result
                = rideRequestRepository.findUpcomingScheduled(now, threshold);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("3 - Should not return rides scheduled before now")
    void shouldNotReturnPastRides() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(15);

        User creator = getExistingUser();

        RideRequest request = createValidRideRequest(creator);
        request.setScheduledTime(now.minusMinutes(5));
        entityManager.flush();

        List<RideRequest> result
                = rideRequestRepository.findUpcomingScheduled(now, threshold);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("4 - Should not return rides after threshold")
    void shouldNotReturnAfterThreshold() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(15);

        User creator = getExistingUser();

        RideRequest request = createValidRideRequest(creator);
        request.setScheduledTime(now.plusMinutes(30));
        entityManager.flush();

        List<RideRequest> result
                = rideRequestRepository.findUpcomingScheduled(now, threshold);

        assertThat(result).isEmpty();
    }

    private User getExistingUser() {
        return userRepository.findByEmail("user@test.com")
                .orElseThrow();
    }

    private RideRequest createValidRideRequest(User creator) {

        VehicleType vehicleType
                = vehicleTypeRepository.findByType("STANDARD")
                        .orElseThrow();

        RideRoute route = new RideRoute();
        route = entityManager.persistAndFlush(route);

        RideRequest request = new RideRequest();
        request.setCreator(creator);
        request.setRideRoute(route);
        request.setStatus(RideRequestStatus.ACCEPTED);
        request.setScheduleType(ScheduleType.LATER);
        request.setScheduledTime(LocalDateTime.now().plusMinutes(5));
        request.setVehicleType(vehicleType);
        request.setBabyTransport(false);
        request.setPetTransport(false);
        request.setCalculatedPrice(500.0);
        request.setLinkedPassengerEmails(List.of("test@mail.com"));

        return entityManager.persistAndFlush(request);
    }

}
