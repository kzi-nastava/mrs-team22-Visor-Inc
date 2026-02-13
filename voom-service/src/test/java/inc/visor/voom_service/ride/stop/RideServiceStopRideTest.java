package inc.visor.voom_service.ride.stop;

import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.service.RideService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ride Service Unit Tests - Stop Ride Functionality")
public class RideServiceStopRideTest {

    @InjectMocks
    private RideService rideService;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverService driverService;

    @Test
    @DisplayName("Should successfully retrieve ride by ID")
    void shouldSuccessfullyRetrieveRideById() {
        // Arrange
        Long rideId = 1L;
        Ride expectedRide = createMockRide(rideId);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(expectedRide));

        // Act
        Optional<Ride> result = rideService.getRide(rideId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(rideId, result.get().getId());
        assertEquals(RideStatus.ONGOING, result.get().getStatus());
        verify(rideRepository, times(1)).findById(rideId);
    }

    @Test
    @DisplayName("Should return empty when ride not found")
    void shouldReturnEmptyWhenRideNotFound() {
        // Arrange
        Long rideId = 999L;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        // Act
        Optional<Ride> result = rideService.getRide(rideId);

        // Assert
        assertFalse(result.isPresent());
        verify(rideRepository, times(1)).findById(rideId);
    }

    @Test
    @DisplayName("Should successfully update ride")
    void shouldSuccessfullyUpdateRide() {
        // Arrange
        Ride ride = createMockRide(1L);
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(LocalDateTime.now());

        when(rideRepository.save(ride)).thenReturn(ride);

        // Act
        Ride result = rideService.update(ride);

        // Assert
        assertNotNull(result);
        assertEquals(RideStatus.STOPPED, result.getStatus());
        assertNotNull(result.getFinishedAt());
        verify(rideRepository, times(1)).save(ride);
    }

    @Test
    @DisplayName("Should handle null ride gracefully")
    void shouldHandleNullRideGracefully() {
        // Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Ride> result = rideService.getRide(123L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should preserve ride data when updating")
    void shouldPreserveRideDataWhenUpdating() {
        // Arrange
        Ride ride = createMockRide(1L);
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(10);
        ride.setStartedAt(startTime);
        ride.setStatus(RideStatus.ONGOING);

        // Update to stopped
        LocalDateTime finishTime = LocalDateTime.now();
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(finishTime);

        when(rideRepository.save(ride)).thenReturn(ride);

        // Act
        Ride result = rideService.update(ride);

        // Assert
        assertNotNull(result);
        assertEquals(RideStatus.STOPPED, result.getStatus());
        assertEquals(startTime, result.getStartedAt());
        assertEquals(finishTime, result.getFinishedAt());
        verify(rideRepository, times(1)).save(ride);
    }

    @Test
    @DisplayName("Should update ride status from ONGOING to STOPPED")
    void shouldUpdateRideStatusFromOngoingToStopped() {
        // Arrange
        Ride ride = createMockRide(1L);
        ride.setStatus(RideStatus.ONGOING);
        assertNull(ride.getFinishedAt());

        // Change status
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(LocalDateTime.now());

        when(rideRepository.save(ride)).thenReturn(ride);

        // Act
        Ride result = rideService.update(ride);

        // Assert
        assertNotNull(result);
        assertEquals(RideStatus.STOPPED, result.getStatus());
        assertNotNull(result.getFinishedAt());
        verify(rideRepository, times(1)).save(ride);
    }

    @Test
    @DisplayName("Should retrieve ride with all associated data")
    void shouldRetrieveRideWithAllAssociatedData() {
        Long rideId = 1L;
        Ride ride = createMockRide(rideId);

        RideRequest rideRequest = new RideRequest();
        RideRoute rideRoute = new RideRoute();
        rideRoute.setRoutePoints(createMockRoutePoints());
        rideRequest.setRideRoute(rideRoute);
        ride.setRideRequest(rideRequest);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        Optional<Ride> result = rideService.getRide(rideId);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getRideRequest());
        assertNotNull(result.get().getRideRequest().getRideRoute());
        assertFalse(result.get().getRideRequest().getRideRoute().getRoutePoints().isEmpty());
        verify(rideRepository, times(1)).findById(rideId);
    }

    @Test
    @DisplayName("Should handle ride update with null finished time")
    void shouldHandleRideUpdateWithNullFinishedTime() {
        Ride ride = createMockRide(1L);
        ride.setStatus(RideStatus.ONGOING);
        ride.setFinishedAt(null);

        when(rideRepository.save(ride)).thenReturn(ride);

        Ride result = rideService.update(ride);

        assertNotNull(result);
        assertEquals(RideStatus.ONGOING, result.getStatus());
        assertNull(result.getFinishedAt());
        verify(rideRepository, times(1)).save(ride);
    }

    @Test
    @DisplayName("Should successfully save ride")
    void shouldSuccessfullySaveRide() {
        Ride ride = createMockRide(1L);
        Ride savedRide = createMockRide(1L);

        when(rideRepository.save(ride)).thenReturn(savedRide);

        Ride result = rideService.save(ride);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(rideRepository, times(1)).save(ride);
    }

    // Helper methods
    private Ride createMockRide(Long id) {
        Ride ride = new Ride();
        ride.setId(id);
        ride.setStatus(RideStatus.ONGOING);
        ride.setStartedAt(LocalDateTime.now().minusMinutes(5));

        Driver driver = new Driver();
        driver.setId(10L);
        driver.setStatus(DriverStatus.BUSY);
        ride.setDriver(driver);

        return ride;
    }

    private List<RoutePoint> createMockRoutePoints() {
        List<RoutePoint> points = new ArrayList<>();

        RoutePoint pickup = new RoutePoint();
        pickup.setOrderIndex(0);
        pickup.setLatitude(45.2458);
        pickup.setLongitude(19.8529);
        pickup.setAddress("Pickup Location");

        RoutePoint dropoff = new RoutePoint();
        dropoff.setOrderIndex(1);
        dropoff.setLatitude(45.2556);
        dropoff.setLongitude(19.8449);
        dropoff.setAddress("Dropoff Location");

        points.add(pickup);
        points.add(dropoff);

        return points;
    }
}