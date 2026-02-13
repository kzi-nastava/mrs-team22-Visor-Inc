package inc.visor.voom_service.ride.stop;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.service.RideEstimateService;
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

    @InjectMocks
    private DriverService driverService;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private RideEstimateService rideEstimateService;

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

    @Test
    @DisplayName("Should successfully get driver from user ID")
    void shouldSuccessfullyGetDriverFromUserId() {
        // Arrange
        Long userId = 1L;
        Driver expectedDriver = createMockDriver(10L, userId);

        when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(expectedDriver));

        // Act
        Optional<Driver> result = driverService.getDriverFromUser(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        assertEquals(userId, result.get().getUser().getId());
        assertEquals(DriverStatus.BUSY, result.get().getStatus());
        verify(driverRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return empty when driver not found for user")
    void shouldReturnEmptyWhenDriverNotFoundForUser() {
        // Arrange
        Long userId = 999L;
        when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        Optional<Driver> result = driverService.getDriverFromUser(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(driverRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should successfully save driver with updated status")
    void shouldSuccessfullySaveDriverWithUpdatedStatus() {
        // Arrange
        Driver driver = createMockDriver(10L, 1L);
        driver.setStatus(DriverStatus.AVAILABLE);

        when(driverRepository.save(driver)).thenReturn(driver);

        // Act
        Driver result = driverService.save(driver);

        // Assert
        assertNotNull(result);
        assertEquals(DriverStatus.AVAILABLE, result.getStatus());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    @DisplayName("Should change driver status from BUSY to AVAILABLE")
    void shouldChangeDriverStatusFromBusyToAvailable() {
        // Arrange
        Driver driver = createMockDriver(10L, 1L);
        driver.setStatus(DriverStatus.BUSY);

        // Change status
        driver.setStatus(DriverStatus.AVAILABLE);

        when(driverRepository.save(driver)).thenReturn(driver);

        // Act
        Driver result = driverService.save(driver);

        // Assert
        assertNotNull(result);
        assertEquals(DriverStatus.AVAILABLE, result.getStatus());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    @DisplayName("Should preserve driver data when updating status")
    void shouldPreserveDriverDataWhenUpdatingStatus() {
        // Arrange
        Driver driver = createMockDriver(10L, 1L);
        Long originalId = driver.getId();
        User originalUser = driver.getUser();
        DriverStatus originalStatus = driver.getStatus();

        // Change only status
        driver.setStatus(DriverStatus.AVAILABLE);

        when(driverRepository.save(driver)).thenReturn(driver);

        // Act
        Driver result = driverService.save(driver);

        // Assert
        assertNotNull(result);
        assertEquals(originalId, result.getId());
        assertEquals(originalUser, result.getUser());
        assertNotEquals(originalStatus, result.getStatus());
        assertEquals(DriverStatus.AVAILABLE, result.getStatus());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    @DisplayName("Should successfully retrieve driver by user ID")
    void shouldSuccessfullyRetrieveDriverByUserId() {
        // Arrange
        Long userId = 5L;
        Driver driver = createMockDriver(20L, userId);

        when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));

        // Act
        Optional<Driver> result = driverService.getDriverFromUser(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(20L, result.get().getId());
        assertEquals(userId, result.get().getUser().getId());
        verify(driverRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return driver with BUSY status when ride is ongoing")
    void shouldReturnDriverWithBusyStatusWhenRideIsOngoing() {
        // Arrange
        Long userId = 1L;
        Driver driver = createMockDriver(10L, userId);
        driver.setStatus(DriverStatus.BUSY);

        when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));

        // Act
        Optional<Driver> result = driverService.getDriverFromUser(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(DriverStatus.BUSY, result.get().getStatus());
        verify(driverRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should update driver and return AVAILABLE status after ride stops")
    void shouldUpdateDriverAndReturnAvailableStatusAfterRideStops() {
        // Arrange
        Driver driver = createMockDriver(10L, 1L);
        driver.setStatus(DriverStatus.BUSY);

        // Simulate stopping ride
        driver.setStatus(DriverStatus.AVAILABLE);

        when(driverRepository.save(driver)).thenReturn(driver);

        // Act
        Driver result = driverService.save(driver);

        // Assert
        assertNotNull(result);
        assertEquals(DriverStatus.AVAILABLE, result.getStatus());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    @DisplayName("Should find driver by user ID for multiple users")
    void shouldFindDriverByUserIdForMultipleUsers() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Driver driver1 = createMockDriver(10L, userId1);
        Driver driver2 = createMockDriver(20L, userId2);

        when(driverRepository.findByUserId(userId1)).thenReturn(Optional.of(driver1));
        when(driverRepository.findByUserId(userId2)).thenReturn(Optional.of(driver2));

        // Act
        Optional<Driver> result1 = driverService.getDriverFromUser(userId1);
        Optional<Driver> result2 = driverService.getDriverFromUser(userId2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(10L, result1.get().getId());
        assertEquals(20L, result2.get().getId());
        assertNotEquals(result1.get().getId(), result2.get().getId());
        verify(driverRepository, times(1)).findByUserId(userId1);
        verify(driverRepository, times(1)).findByUserId(userId2);
    }

    @Test
    @DisplayName("Should not change driver ID when updating status")
    void shouldNotChangeDriverIdWhenUpdatingStatus() {
        // Arrange
        Long driverId = 10L;
        Driver driver = createMockDriver(driverId, 1L);
        driver.setStatus(DriverStatus.BUSY);

        driver.setStatus(DriverStatus.AVAILABLE);

        when(driverRepository.save(driver)).thenReturn(driver);

        // Act
        Driver result = driverService.save(driver);

        // Assert
        assertNotNull(result);
        assertEquals(driverId, result.getId());
        verify(driverRepository, times(1)).save(driver);
    }

    // Helper method
    private Driver createMockDriver(Long driverId, Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("driver" + userId + "@test.com");

        Driver driver = new Driver();
        driver.setId(driverId);
        driver.setUser(user);
        driver.setStatus(DriverStatus.BUSY);

        return driver;
    }
}