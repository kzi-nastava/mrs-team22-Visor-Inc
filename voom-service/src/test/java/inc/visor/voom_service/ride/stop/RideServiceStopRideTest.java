package inc.visor.voom_service.ride.stop;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.model.*;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.service.RideEstimateService;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.vehicle.model.VehicleType;
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

    @Test
    @DisplayName("Should calculate total distance for route points")
    void shouldCalculateTotalDistanceForRoutePoints() {
        // Arrange
        List<RideRequestCreateDto.RoutePointDto> points = createRoutePoints(
                45.2458, 19.8529,  // Pickup
                45.2556, 19.8449   // Dropoff
        );

        // Act
        double distance = rideEstimateService.calculateTotalDistance(points);

        // Assert
        assertTrue(distance > 0);
        assertTrue(distance < 2.0); // Approx 1.5 km for these coordinates
    }

    @Test
    @DisplayName("Should return zero distance for single point")
    void shouldReturnZeroDistanceForSinglePoint() {
        // Arrange
        List<RideRequestCreateDto.RoutePointDto> points = new ArrayList<>();
        RideRequestCreateDto.RoutePointDto point = new RideRequestCreateDto.RoutePointDto();
        point.lat = 45.2458;
        point.lng = 19.8529;
        points.add(point);

        // Act
        double distance = rideEstimateService.calculateTotalDistance(points);

        // Assert
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    @DisplayName("Should handle empty route points list")
    void shouldHandleEmptyRoutePointsList() {
        // Arrange
        List<RideRequestCreateDto.RoutePointDto> points = new ArrayList<>();

        // Act
        double distance = rideEstimateService.calculateTotalDistance(points);

        // Assert
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    @DisplayName("Should estimate ride price based on distance and vehicle type")
    void shouldEstimateRidePriceBasedOnDistanceAndVehicleType() {
        // Arrange
        VehicleType standardType = createVehicleType("STANDARD", 150.0);
        List<RideRequestCreateDto.RoutePointDto> points = createRoutePoints(
                45.2458, 19.8529,
                45.2556, 19.8449
        );

        // Act
        RideEstimationResult result = rideEstimateService.estimate(points, standardType);

        // Assert
        assertNotNull(result);
        assertTrue(result.distanceKm() > 0);
        assertTrue(result.price() > 0);
    }

    @Test
    @DisplayName("Should calculate different prices for different vehicle types")
    void shouldCalculateDifferentPricesForDifferentVehicleTypes() {
        // Arrange
        VehicleType standardType = createVehicleType("STANDARD", 150.0);
        VehicleType luxuryType = createVehicleType("LUXURY", 250.0);
        VehicleType vanType = createVehicleType("VAN", 200.0);

        List<RideRequestCreateDto.RoutePointDto> points = createRoutePoints(
                45.2458, 19.8529,
                45.2556, 19.8449
        );

        // Act
        RideEstimationResult standardResult = rideEstimateService.estimate(points, standardType);
        RideEstimationResult luxuryResult = rideEstimateService.estimate(points, luxuryType);
        RideEstimationResult vanResult = rideEstimateService.estimate(points, vanType);

        // Assert
        assertNotNull(standardResult);
        assertNotNull(luxuryResult);
        assertNotNull(vanResult);

        // Same distance for all
        assertEquals(standardResult.distanceKm(), luxuryResult.distanceKm(), 0.01);
        assertEquals(standardResult.distanceKm(), vanResult.distanceKm(), 0.01);

        // Different prices
        assertTrue(luxuryResult.price() > standardResult.price());
        assertTrue(vanResult.price() > standardResult.price());
        assertTrue(luxuryResult.price() > vanResult.price());
    }

    @Test
    @DisplayName("Should calculate distance for multiple route points")
    void shouldCalculateDistanceForMultipleRoutePoints() {
        // Arrange
        List<RideRequestCreateDto.RoutePointDto> points = new ArrayList<>();

        // Point 1: Novi Sad center
        points.add(createRoutePoint(45.2458, 19.8529,0,  RoutePointType.PICKUP));

        // Point 2: Intermediate
        points.add(createRoutePoint(45.2500, 19.8450, 1,  RoutePointType.STOP));

        // Point 3: Final destination
        points.add(createRoutePoint(45.2556, 19.8449, 3, RoutePointType.DROPOFF));

        // Act
        double distance = rideEstimateService.calculateTotalDistance(points);

        // Assert
        assertTrue(distance > 0);
        assertTrue(distance < 3.0); // Reasonable distance for these points
    }

    @Test
    @DisplayName("Should estimate price correctly for long distance")
    void shouldEstimatePriceCorrectlyForLongDistance() {
        // Arrange
        VehicleType standardType = createVehicleType("STANDARD", 150.0);
        List<RideRequestCreateDto.RoutePointDto> points = createRoutePoints(
                45.2458, 19.8529,  // Novi Sad
                44.8125, 20.4612   // Belgrade (approx 80km)
        );

        // Act
        RideEstimationResult result = rideEstimateService.estimate(points, standardType);

        // Assert
        assertNotNull(result);
        assertTrue(result.distanceKm() < 100); // Less than 100km
    }

    @Test
    @DisplayName("Should handle zero distance gracefully")
    void shouldHandleZeroDistanceGracefully() {
        // Arrange
        VehicleType standardType = createVehicleType("STANDARD", 150.0);
        List<RideRequestCreateDto.RoutePointDto> points = createRoutePoints(
                45.2458, 19.8529,
                45.2458, 19.8529  // Same point
        );

        // Act
        RideEstimationResult result = rideEstimateService.estimate(points, standardType);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.distanceKm(), 0.01);
        assertEquals(150.0, result.price(), 0.01);
    }

    @Test
    @DisplayName("Should calculate price proportional to distance")
    void shouldCalculatePriceProportionalToDistance() {
        // Arrange
        VehicleType standardType = createVehicleType("STANDARD", 150.0);

        // Short distance
        List<RideRequestCreateDto.RoutePointDto> shortPoints = createRoutePoints(
                45.2458, 19.8529,
                45.2468, 19.8539
        );

        // Long distance
        List<RideRequestCreateDto.RoutePointDto> longPoints = createRoutePoints(
                45.2458, 19.8529,
                45.2656, 19.8749
        );

        // Act
        RideEstimationResult shortResult = rideEstimateService.estimate(shortPoints, standardType);
        RideEstimationResult longResult = rideEstimateService.estimate(longPoints, standardType);

        // Assert
        assertTrue(longResult.distanceKm() > shortResult.distanceKm());
        assertTrue(longResult.price() > shortResult.price());
    }

    @Test
    @DisplayName("Should handle route with same pickup and dropoff")
    void shouldHandleRouteWithSamePickupAndDropoff() {
        // Arrange
        VehicleType standardType = createVehicleType("STANDARD", 150.0);
        List<RideRequestCreateDto.RoutePointDto> points = createRoutePoints(
                45.2458, 19.8529,
                45.2458, 19.8529
        );

        // Act
        RideEstimationResult result = rideEstimateService.estimate(points, standardType);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.distanceKm(), 0.001);
        assertEquals(150.0, result.price(), 0.001);
    }

    @Test
    @DisplayName("Should calculate correct distance using Haversine formula")
    void shouldCalculateCorrectDistanceUsingHaversineFormula() {
        // Arrange
        List<RideRequestCreateDto.RoutePointDto> points = createRoutePoints(
                45.2458, 19.8529,  // Novi Sad
                45.2556, 19.8449   // Nearby location
        );

        // Act
        double distance = rideEstimateService.calculateTotalDistance(points);

        // Assert
        // Expected distance ~1.3 km
        assertTrue(distance > 1.0);
        assertTrue(distance < 2.0);
    }

    @Test
    @DisplayName("Should handle null vehicle type gracefully")
    void shouldHandleNullVehicleTypeGracefully() {
        // Arrange
        List<RideRequestCreateDto.RoutePointDto> points = createRoutePoints(
                45.2458, 19.8529,
                45.2556, 19.8449
        );

        // Act & Assert
        assertThrows(Exception.class, () -> {
            rideEstimateService.estimate(points, null);
        });
    }

    @Test
    @DisplayName("Should estimate for route with multiple stops correctly")
    void shouldEstimateForRouteWithMultipleStopsCorrectly() {
        // Arrange
        VehicleType standardType = createVehicleType("STANDARD", 150.0);
        List<RideRequestCreateDto.RoutePointDto> points = new ArrayList<>();

        points.add(createRoutePoint(45.2458, 19.85291,0,  RoutePointType.PICKUP)); // Start
        points.add(createRoutePoint(45.2478, 19.8549,1,  RoutePointType.STOP)); // Stop 1
        points.add(createRoutePoint(45.2498, 19.8569,2,  RoutePointType.STOP)); // Stop 2
        points.add(createRoutePoint(45.2518, 19.8589, 3, RoutePointType.DROPOFF)); // End

        // Act
        RideEstimationResult result = rideEstimateService.estimate(points, standardType);

        // Assert
        assertNotNull(result);
        assertTrue(result.distanceKm() > 0);
        assertTrue(result.price() > 0);
    }

    // Helper methods
    private VehicleType createVehicleType(String name, Double price) {
        VehicleType type = new VehicleType();
        type.setId(1L);
        type.setType(name);
        type.setPrice(price);
        return type;
    }

    private List<RideRequestCreateDto.RoutePointDto> createRoutePoints(
            double lat1, double lng1, double lat2, double lng2) {
        List<RideRequestCreateDto.RoutePointDto> points = new ArrayList<>();
        points.add(createRoutePoint(lat1, lng1, 0, RoutePointType.PICKUP));
        points.add(createRoutePoint(lat2, lng2, 1, RoutePointType.DROPOFF));
        return points;
    }

    private RideRequestCreateDto.RoutePointDto createRoutePoint(double lat, double lng, int orderIndex, RoutePointType type) {
        RideRequestCreateDto.RoutePointDto point = new RideRequestCreateDto.RoutePointDto();
        point.lat = lat;
        point.lng = lng;
        point.address = "";
        point.orderIndex = orderIndex;
        point.type = type.toString();
        return point;
    }
}