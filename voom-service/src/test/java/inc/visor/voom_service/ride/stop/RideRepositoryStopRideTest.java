package inc.visor.voom_service.ride.stop;

import inc.visor.voom_service.auth.user.model.Permission;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverState;
import inc.visor.voom_service.driver.model.DriverStateChange;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.vehicle.model.Vehicle;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Ride Repository Unit Tests - Stop Ride Functionality")
public class RideRepositoryStopRideTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RideRepository rideRepository;

    private Person userPerson;
    private User creatorUser;
    private Driver driver;
    private RideRequest rideRequest;
    private Ride ride;
    private LocalDateTime baseTime;
    private VehicleType type;
    private UserRole userRole;
    private DriverStateChange initChange;
    private UserRole driverRole;
    private User driverUser;
    private Person driverPerson;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.now();

        userRole = new UserRole();
        userRole.setRoleName("USER");
        userRole.setPermissions(Set.of(new Permission("USER")));

        driverRole = new UserRole();
        driverRole.setRoleName("DRIVER");
        driverRole.setPermissions(Set.of(new Permission("DRIVER")));

        type = new VehicleType();
        type.setType("VAN");
        type.setPrice(10.0);
        type = entityManager.persist(type);

        //SETUP DRIVER

        driverPerson = new Person();
        driverPerson.setFirstName("Driver 1");
        driverPerson.setLastName("Lastname 1");
        driverPerson.setAddress("Novi Sad, Street 1");
        driverPerson.setPhoneNumber("+38160123456");
        driverPerson.setBirthDate(LocalDateTime.of(1980, 1, 1, 0, 0));
        driverPerson = entityManager.persist(driverPerson);

        driverUser = new User();
        driverUser.setEmail("driver1@gmail.com");
        driverUser.setPassword("test1234");
        driverUser.setUserRole(driverRole);
        driverUser.setUserStatus(UserStatus.ACTIVE);
        driverUser.setPerson(driverPerson);
        driverUser = entityManager.persistAndFlush(driverUser);

        driver = new Driver();
        driver.setUser(driverUser);
        driver.setStatus(DriverStatus.AVAILABLE);
        driver = entityManager.persistAndFlush(driver);

        initChange = new DriverStateChange();
        initChange.setDriver(driver);
        initChange.setCurrentState(DriverState.ACTIVE);
        initChange.setPerformedAt(LocalDateTime.now().minusSeconds(5));
        initChange = entityManager.persistAndFlush(initChange);

        vehicle = new Vehicle();
        vehicle.setDriver(driver);
        vehicle.setModel("Volkswagen Passat");
        vehicle.setLicensePlate("NS-101-AB");
        vehicle.setYear(2017);
        vehicle.setBabySeat(true);
        vehicle.setPetFriendly(true);
        vehicle.setNumberOfSeats(4);
        vehicle.setVehicleType(type);
        vehicle = entityManager.persistAndFlush(vehicle);

        userPerson = new Person();
        userPerson.setFirstName("User 1");
        userPerson.setLastName("Lastname 1");
        userPerson.setAddress("Novi Sad, Street 1");
        userPerson.setPhoneNumber("+38160123456");
        userPerson.setBirthDate(LocalDateTime.of(1980, 1, 1, 0, 0));
        userPerson = entityManager.persistAndFlush(userPerson);

        creatorUser = new User();
        creatorUser.setEmail("user1@gmail.com");
        creatorUser.setPassword("test1234");
        creatorUser.setUserRole(userRole);
        creatorUser.setUserStatus(UserStatus.ACTIVE);
        creatorUser.setPerson(userPerson);
        creatorUser = entityManager.persistAndFlush(creatorUser);

        RideRoute route = new RideRoute();
        Double[] coords = {45.2458, 19.8529, 45.2556, 19.8449};

        RoutePoint pickup = createPoint(
                "Pickup Street",
                coords[0],
                coords[1]
        );
        pickup.setPointType(RoutePointType.PICKUP);

        RoutePoint dropoff = createPoint(
                "Dropoff Street",
                coords[2],
                coords[3]
        );
        dropoff.setPointType(RoutePointType.DROPOFF);

        route.setRoutePoints(Arrays.asList(pickup, dropoff));
        route.setTotalDistanceKm(
                calculateDistanceKm(
                        coords[0], coords[1],
                        coords[2], coords[3]
                )
        );

        rideRequest = new RideRequest();
        rideRequest.setCreator(creatorUser);
        rideRequest.setRideRoute(route);
        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        rideRequest.setScheduleType(ScheduleType.NOW);
        rideRequest.setVehicleType(type);
        rideRequest.setCalculatedPrice(8 + Math.random() * 5);

        // Setup ride
        ride = new Ride();
        ride.setRideRequest(rideRequest);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ONGOING);
        ride.setStartedAt(baseTime.minusMinutes(1));
        ride = entityManager.persistAndFlush(ride);

        entityManager.clear();
    }

    private double calculateDistanceKm(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        final int R = 6371; // Earth radius km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);

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
        // Arrange
        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest);
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

    }

    @Test
    @DisplayName("Should return empty list when no rides with specified status exist")
    void shouldReturnEmptyListWhenNoRidesWithStatusExist() {
        List<Ride> results = rideRepository.findByStatus(RideStatus.USER_CANCELLED);
        assertTrue(results.isEmpty());
        results = rideRepository.findByStatus(RideStatus.DRIVER_CANCELLED);
        assertTrue(results.isEmpty());
        results = rideRepository.findByStatus(RideStatus.STOPPED);
        assertTrue(results.isEmpty());
        results = rideRepository.findByStatus(RideStatus.PANIC);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by multiple statuses")
    void shouldFindRidesByMultipleStatuses() {

    }

    @Test
    @DisplayName("Should return empty list when no rides with specified statuses exist")
    void shouldReturnEmptyListWhenNoRidesWithSpecifiedStatusesExist() {
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
    @DisplayName("Should return empty list when no rides exist for creator")
    void shouldReturnEmptyListWhenNoRidesExistForCreator() {
        List<Ride> results = rideRepository.findByRideRequest_Creator_Id(999L);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by driver user ID")
    void shouldFindRidesByDriverUserId() {
        List<Ride> results = rideRepository.findByDriver_User_Id(driverUser.getId());
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(r ->r.getDriver().getUser().getId() == driverUser.getId()));
    }

    @Test
    @DisplayName("Should return empty list when no rides exist for driver user ID")
    void shouldReturnEmptyListWhenNoRidesExistForDriverUserId() {

    }

    @Test
    @DisplayName("Should find rides by creator ID and status")
    void shouldFindRidesByCreatorIdAndStatus() {

    }

    @Test
    @DisplayName("Should return empty list when no rides match creator and status")
    void shouldReturnEmptyListWhenNoRidesMatchCreatorAndStatus() {
        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatus(creatorUser.getId(), RideStatus.USER_CANCELLED);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find rides by creator, status, and finished time range")
    void shouldFindRidesByCreatorStatusAndFinishedTimeRange() {

    }

    @Test
    @DisplayName("Should return empty list when no rides match time range")
    void shouldReturnEmptyListWhenNoRidesMatchTimeRange() {
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(baseTime.minusDays(10));
        entityManager.persistAndFlush(ride);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween(creatorUser.getId(), RideStatus.STOPPED, from, to);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle boundary conditions for time range query")
    void shouldHandleBoundaryConditionsForTimeRangeQuery() {
        LocalDateTime exactTime = baseTime.minusHours(1);
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(exactTime);
        entityManager.persistAndFlush(ride);
        entityManager.clear();

        LocalDateTime from = exactTime.minusSeconds(1);
        LocalDateTime to = exactTime.plusSeconds(1);
        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween(creatorUser.getId(), RideStatus.STOPPED, from, to);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Should find rides by driver, status, and finished time range")
    void shouldFindRidesByDriverStatusAndFinishedTimeRange() {

    }

    @Test
    @DisplayName("Should return empty list when no driver rides match time range")
    void shouldReturnEmptyListWhenNoDriverRidesMatchTimeRange() {
        ride.setStatus(RideStatus.FINISHED);
        ride.setFinishedAt(baseTime.minusDays(10));
        entityManager.persistAndFlush(ride);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByDriver_IdAndStatusAndFinishedAtBetween(driver.getId(), RideStatus.FINISHED, from, to);

        assertTrue(results.isEmpty());
    }
    @Test
    @DisplayName("Should find all rides by status and finished time range")
    void shouldFindAllRidesByStatusAndFinishedTimeRange() {

    }

    @Test
    @DisplayName("Should return empty list when no rides match global time range")
    void shouldReturnEmptyListWhenNoRidesMatchGlobalTimeRange() {
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(baseTime.minusDays(10));
        entityManager.persistAndFlush(ride);
        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(3);
        LocalDateTime to = baseTime;

        List<Ride> results = rideRepository.findByStatusAndFinishedAtBetween(RideStatus.STOPPED, from, to);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple statuses with different time ranges")
    void shouldHandleMultipleStatusesWithDifferentTimeRanges() {
        ride.setStatus(RideStatus.STOPPED);
        ride.setFinishedAt(baseTime.minusHours(1));
        entityManager.persistAndFlush(ride);

        Ride ride2 = new Ride();
        ride2.setDriver(driver);
        ride2.setRideRequest(rideRequest);
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setStartedAt(baseTime.minusHours(2));
        ride2.setFinishedAt(baseTime.minusHours(1).minusMinutes(30));
        entityManager.persistAndFlush(ride2);

        entityManager.clear();

        LocalDateTime from = baseTime.minusHours(2);
        LocalDateTime to = baseTime;

        List<Ride> stoppedRides = rideRepository.findByStatusAndFinishedAtBetween(RideStatus.STOPPED, from, to);
        List<Ride> completedRides = rideRepository.findByStatusAndFinishedAtBetween(RideStatus.FINISHED, from, to);

        assertEquals(1, stoppedRides.size());
        assertEquals(1, completedRides.size());
        assertNotEquals(stoppedRides.getFirst().getId(), completedRides.getFirst().getId());
    }
}
