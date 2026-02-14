package inc.visor.voom_service.ride.stop;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.vehicle.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Ride Repository Unit Tests - Stop Ride Functionality")
public class RideRepositoryStopRideTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RideRepository rideRepository;

    private User creatorUser;
    private Driver driver;
    private RideRequest rideRequest;
    private Ride ride;
    private LocalDateTime baseTime;
    private VehicleType vehicleType;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.now();

        // Load existing test data from test-data.sql
        creatorUser = entityManager.find(User.class, 1L); // user@test.com
        driver = entityManager.find(Driver.class, 1L); // driver@test.com
        vehicleType = entityManager.find(VehicleType.class, 1L); // STANDARD

        // Create ride route
        RideRoute route = createRideRoute();

        // Create ride request
        rideRequest = new RideRequest();
        rideRequest.setCreator(creatorUser);
        rideRequest.setRideRoute(route);
        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        rideRequest.setScheduleType(ScheduleType.NOW);
        rideRequest.setVehicleType(vehicleType);
        rideRequest.setCalculatedPrice(1500.0);
        rideRequest = entityManager.persistAndFlush(rideRequest);

        // Create ride
        ride = new Ride();
        ride.setRideRequest(rideRequest);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ONGOING);
        ride.setStartedAt(baseTime.minusMinutes(1));
        ride = entityManager.persistAndFlush(ride);

        entityManager.clear();
    }

    private RideRoute createRideRoute() {
        RideRoute route = new RideRoute();
        Double[] coords = {45.2458, 19.8529, 45.2556, 19.8449};

        RoutePoint pickup = createPoint("Pickup Street", coords[0], coords[1]);
        pickup.setPointType(RoutePointType.PICKUP);
        pickup = entityManager.persist(pickup);

        RoutePoint dropoff = createPoint("Dropoff Street", coords[2], coords[3]);
        dropoff.setPointType(RoutePointType.DROPOFF);
        dropoff = entityManager.persist(dropoff);

        route.setRoutePoints(Arrays.asList(pickup, dropoff));
        route.setTotalDistanceKm(calculateDistanceKm(coords[0], coords[1], coords[2], coords[3]));
        return entityManager.persist(route);
    }

    private RideRequest createNewRideRequest() {
        RideRoute route = createRideRoute();

        RideRequest newRequest = new RideRequest();
        newRequest.setCreator(creatorUser);
        newRequest.setRideRoute(route);
        newRequest.setStatus(RideRequestStatus.ACCEPTED);
        newRequest.setScheduleType(ScheduleType.NOW);
        newRequest.setVehicleType(vehicleType);
        newRequest.setCalculatedPrice(1000.0 + Math.random() * 500);
        return entityManager.persist(newRequest);
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private RoutePoint createPoint(String address, double lat, double lng) {
        RoutePoint point = new RoutePoint();
        point.setAddress(address);
        point.setLatitude(lat);
        point.setLongitude(lng);
        return point;
    }

    // ==================== TESTS ====================

    @Test
    @DisplayName("Should find ride by ride request ID")
    void shouldFindRideByRideRequestId() {
        Optional<Ride> result = rideRepository.findByRideRequestId(rideRequest.getId());

        assertTrue(result.isPresent());
        assertEquals(ride.getId(), result.get().getId());
        assertEquals(rideRequest.getId(), result.get().getRideRequest().getId());
    }

    @Test
    @DisplayName("Should return empty when no ride exists for ride request ID")
    void shouldReturnEmptyWhenNoRideExistsForRideRequestId() {
        Optional<Ride> result = rideRepository.findByRideRequestId(999L);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find rides by driver ID")
    void shouldFindRidesByDriverId() {
        RideRequest rideRequest2 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setStartedAt(baseTime.minusHours(2));
        ride2.setFinishedAt(baseTime.minusHours(1));
        entityManager.persistAndFlush(ride2);
        entityManager.clear();

        List<Ride> results = rideRepository.findByDriverId(driver.getId());

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.getDriver().getId() == driver.getId()));
    }

    @Test
    @DisplayName("Should return empty list when no rides exist for driver")
    void shouldReturnEmptyListWhenNoRidesExistForDriver() {
        List<Ride> results = rideRepository.findByDriverId(999L);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by status")
    void shouldFindRidesByStatus() {
        RideRequest rideRequest2 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.ONGOING);
        ride2.setStartedAt(baseTime.minusMinutes(15));
        entityManager.persistAndFlush(ride2);
        entityManager.clear();

        List<Ride> results = rideRepository.findByStatus(RideStatus.ONGOING);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.getStatus() == RideStatus.ONGOING));
    }

    @Test
    @DisplayName("Should return empty list when no rides with specified status exist")
    void shouldReturnEmptyListWhenNoRidesWithStatusExist() {
        List<Ride> results = rideRepository.findByStatus(RideStatus.USER_CANCELLED);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by multiple statuses")
    void shouldFindRidesByMultipleStatuses() {
        RideRequest rideRequest2 = createNewRideRequest();
        RideRequest rideRequest3 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setStartedAt(baseTime.minusHours(2));
        ride2.setFinishedAt(baseTime.minusHours(1));
        entityManager.persistAndFlush(ride2);

        Ride ride3 = new Ride();
        ride3.setDriver(driver);
        ride3.setRideRequest(rideRequest3);
        ride3.setStatus(RideStatus.STOPPED);
        ride3.setStartedAt(baseTime.minusHours(3));
        ride3.setFinishedAt(baseTime.minusHours(2));
        entityManager.persistAndFlush(ride3);
        entityManager.clear();

        List<RideStatus> statuses = Arrays.asList(RideStatus.FINISHED, RideStatus.STOPPED);
        List<Ride> results = rideRepository.findByStatusIn(statuses);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r ->
                r.getStatus() == RideStatus.FINISHED || r.getStatus() == RideStatus.STOPPED));
    }

    @Test
    @DisplayName("Should return empty list when no rides with specified statuses exist")
    void shouldReturnEmptyListWhenNoRidesWithSpecifiedStatusesExist() {
        List<RideStatus> statuses = Arrays.asList(RideStatus.USER_CANCELLED, RideStatus.DRIVER_CANCELLED);
        List<Ride> results = rideRepository.findByStatusIn(statuses);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should get ride by ID")
    void shouldGetRideById() {
        Optional<Ride> result = rideRepository.getRideById(ride.getId());

        assertTrue(result.isPresent());
        assertEquals(ride.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty when ride with ID does not exist")
    void shouldReturnEmptyWhenRideWithIdDoesNotExist() {
        Optional<Ride> result = rideRepository.getRideById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find rides by creator ID")
    void shouldFindRidesByCreatorId() {
        RideRequest rideRequest2 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setStartedAt(baseTime.minusHours(1));
        ride2.setFinishedAt(baseTime.minusMinutes(30));
        entityManager.persistAndFlush(ride2);
        entityManager.clear();

        List<Ride> results = rideRepository.findByRideRequest_Creator_Id(creatorUser.getId());

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r ->
                r.getRideRequest().getCreator().getId() == creatorUser.getId()));
    }

    @Test
    @DisplayName("Should return empty list when no rides exist for creator")
    void shouldReturnEmptyListWhenNoRidesExistForCreator() {
        List<Ride> results = rideRepository.findByRideRequest_Creator_Id(999L);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by driver user ID")
    void shouldFindRidesByDriverUserId() {
        List<Ride> results = rideRepository.findByDriver_User_Id(driver.getUser().getId());

        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(r ->
                r.getDriver().getUser().getId() == driver.getUser().getId()));
    }

    @Test
    @DisplayName("Should return empty list when no rides exist for driver user ID")
    void shouldReturnEmptyListWhenNoRidesExistForDriverUserId() {
        List<Ride> results = rideRepository.findByDriver_User_Id(999L);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by creator ID and status")
    void shouldFindRidesByCreatorIdAndStatus() {
        ride.setStatus(RideStatus.FINISHED);
        ride.setFinishedAt(baseTime);
        entityManager.merge(ride);

        RideRequest rideRequest2 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.ONGOING);
        ride2.setStartedAt(baseTime.minusMinutes(10));
        entityManager.persistAndFlush(ride2);
        entityManager.clear();

        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatus(
                creatorUser.getId(), RideStatus.FINISHED);

        assertEquals(1, results.size());
        assertEquals(RideStatus.FINISHED, results.getFirst().getStatus());
        assertEquals(creatorUser.getId(), results.getFirst().getRideRequest().getCreator().getId());
    }

    @Test
    @DisplayName("Should return empty list when no rides match creator and status")
    void shouldReturnEmptyListWhenNoRidesMatchCreatorAndStatus() {
        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatus(
                creatorUser.getId(), RideStatus.USER_CANCELLED);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by creator, status, and finished time range")
    void shouldFindRidesByCreatorStatusAndFinishedTimeRange() {
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(baseTime.minusHours(2));
        entityManager.merge(ride);

        RideRequest rideRequest2 = createNewRideRequest();
        RideRequest rideRequest3 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.STOPPED);
        ride2.setStartedAt(baseTime.minusHours(1).minusMinutes(30));
        ride2.setFinishedAt(baseTime.minusHours(1));
        entityManager.persistAndFlush(ride2);

        Ride ride3 = new Ride();
        ride3.setDriver(driver);
        ride3.setRideRequest(rideRequest3);
        ride3.setStatus(RideStatus.STOPPED);
        ride3.setStartedAt(baseTime.minusDays(2));
        ride3.setFinishedAt(baseTime.minusDays(1));
        entityManager.persistAndFlush(ride3);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween(
                creatorUser.getId(), RideStatus.STOPPED, from, to);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r ->
                r.getStatus() == RideStatus.STOPPED &&
                        r.getRideRequest().getCreator().getId() == creatorUser.getId() &&
                        r.getFinishedAt().isAfter(from) && r.getFinishedAt().isBefore(to)));
    }

    @Test
    @DisplayName("Should return empty list when no rides match time range")
    void shouldReturnEmptyListWhenNoRidesMatchTimeRange() {
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(baseTime.minusDays(10));
        entityManager.merge(ride);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween(
                creatorUser.getId(), RideStatus.STOPPED, from, to);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by driver, status, and finished time range")
    void shouldFindRidesByDriverStatusAndFinishedTimeRange() {
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(baseTime.minusHours(2));
        entityManager.merge(ride);

        RideRequest rideRequest2 = createNewRideRequest();
        RideRequest rideRequest3 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.STOPPED);
        ride2.setStartedAt(baseTime.minusHours(1).minusMinutes(30));
        ride2.setFinishedAt(baseTime.minusHours(1));
        entityManager.persistAndFlush(ride2);

        Ride ride3 = new Ride();
        ride3.setDriver(driver);
        ride3.setRideRequest(rideRequest3);
        ride3.setStatus(RideStatus.STOPPED);
        ride3.setStartedAt(baseTime.minusDays(2));
        ride3.setFinishedAt(baseTime.minusDays(1));
        entityManager.persistAndFlush(ride3);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByDriver_IdAndStatusAndFinishedAtBetween(
                driver.getId(), RideStatus.STOPPED, from, to);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r ->
                r.getStatus() == RideStatus.STOPPED &&
                        r.getDriver().getId() == creatorUser.getId() &&
                        r.getFinishedAt().isAfter(from) && r.getFinishedAt().isBefore(to)));
    }

    @Test
    @DisplayName("Should return empty list when no driver rides match time range")
    void shouldReturnEmptyListWhenNoDriverRidesMatchTimeRange() {
        ride.setStatus(RideStatus.FINISHED);
        ride.setFinishedAt(baseTime.minusDays(10));
        entityManager.merge(ride);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByDriver_IdAndStatusAndFinishedAtBetween(
                driver.getId(), RideStatus.FINISHED, from, to);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find all rides by status and finished time range")
    void shouldFindAllRidesByStatusAndFinishedTimeRange() {
        ride.setStatus(RideStatus.FINISHED);
        ride.setFinishedAt(baseTime.minusHours(2));
        entityManager.merge(ride);

        RideRequest rideRequest2 = createNewRideRequest();
        RideRequest rideRequest3 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setStartedAt(baseTime.minusHours(1).minusMinutes(30));
        ride2.setFinishedAt(baseTime.minusHours(1));
        entityManager.persistAndFlush(ride2);

        Ride ride3 = new Ride();
        ride3.setDriver(driver);
        ride3.setRideRequest(rideRequest3);
        ride3.setStatus(RideStatus.FINISHED);
        ride3.setStartedAt(baseTime.minusDays(2));
        ride3.setFinishedAt(baseTime.minusDays(1));
        entityManager.persistAndFlush(ride3);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByStatusAndFinishedAtBetween(
                RideStatus.FINISHED, from, to);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r ->
                r.getStatus() == RideStatus.FINISHED &&
                        r.getFinishedAt().isAfter(from) && r.getFinishedAt().isBefore(to)));
    }

    @Test
    @DisplayName("Should return empty list when no rides match global time range")
    void shouldReturnEmptyListWhenNoRidesMatchGlobalTimeRange() {
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(baseTime.minusDays(10));
        entityManager.merge(ride);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByStatusAndFinishedAtBetween(
                RideStatus.STOPPED, from, to);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple statuses with different time ranges")
    void shouldHandleMultipleStatusesWithDifferentTimeRanges() {
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(baseTime.minusHours(1));
        entityManager.merge(ride);

        RideRequest rideRequest2 = createNewRideRequest();

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest2);
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setStartedAt(baseTime.minusHours(2));
        ride2.setFinishedAt(baseTime.minusHours(1).minusMinutes(30));
        entityManager.persistAndFlush(ride2);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(2);
        LocalDateTime to = baseTime;

        List<Ride> stoppedRides = rideRepository.findByStatusAndFinishedAtBetween(
                RideStatus.STOPPED, from, to);
        List<Ride> completedRides = rideRepository.findByStatusAndFinishedAtBetween(
                RideStatus.FINISHED, from, to);

        assertEquals(1, stoppedRides.size());
        assertEquals(1, completedRides.size());
        assertNotEquals(stoppedRides.getFirst().getId(), completedRides.getFirst().getId());
    }
}