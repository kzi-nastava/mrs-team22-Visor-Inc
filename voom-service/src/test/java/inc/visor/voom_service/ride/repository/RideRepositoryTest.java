package inc.visor.voom_service.ride.repository;

import inc.visor.voom_service.auth.user.model.*;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.vehicle.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RideRepositoryTest {

    @Autowired private RideRepository rideRepository;
    @Autowired private TestEntityManager entityManager;

    private User driverUser;
    private Driver driver;
    private User passenger;

    @BeforeEach
    void setUp() {
        passenger = createAndPersistUser("passenger@gmail.com", "USER");
        driverUser = createAndPersistUser("driver@gmail.com", "DRIVER");
        driver = createAndPersistDriver(driverUser);
    }

    // 1. findByRideRequestId
    @Test
    void testFindByRideRequestId() {
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.ONGOING);
        Long requestId = ride.getRideRequest().getId();

        assertThat(rideRepository.findByRideRequestId(requestId)).isPresent();
        assertThat(rideRepository.findByRideRequestId(999L)).isEmpty();
    }

    @Test
    @DisplayName("findByRideRequestId should return empty for null or incorrect id")
    void testFindByRideRequestId_InvalidIds() {
        Optional<Ride> negativeResult = rideRepository.findByRideRequestId(-1L);
        assertThat(negativeResult).isEmpty();

        Optional<Ride> nullResult = rideRepository.findByRideRequestId(null);
        assertThat(nullResult).isEmpty();
    }

    @Test
    @DisplayName("findByRideRequestId should return empty when ride requrst exists but no ride is linked")
    void testFindByRideRequestId_RequestExistsWithoutRide() {
        RideRoute route = new RideRoute();
        entityManager.persist(route);

        VehicleType vType = new VehicleType();
        vType.setType("vehicle type to kill errors " + java.util.UUID.randomUUID().toString().substring(0, 5));
        vType.setPrice(10.0);
        entityManager.persist(vType);

        RideRequest standaloneRequest = new RideRequest();
        standaloneRequest.setCreator(passenger);
        standaloneRequest.setRideRoute(route);
        standaloneRequest.setVehicleType(vType);
        standaloneRequest.setStatus(RideRequestStatus.PENDING);
        standaloneRequest.setScheduleType(ScheduleType.NOW);

        standaloneRequest = entityManager.persistFlushFind(standaloneRequest);
        Long requestId = standaloneRequest.getId();

        Optional<Ride> result = rideRepository.findByRideRequestId(requestId);

        assertThat(result).isEmpty();
    }

    // 2. findByDriverId
    @Test
    void testFindByDriverId() {
        createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        createAndPersistRide(driver, passenger, RideStatus.USER_CANCELLED);

        assertThat(rideRepository.findByDriverId(driver.getId())).hasSize(2);
        assertThat(rideRepository.findByDriverId(999L)).isEmpty();
    }

    @Test
    @DisplayName("findByDriverId should return empty list for a new driver with no rides")
    void testFindByDriverId_NewDriverNoRides() {
        User secondDriverUser = createAndPersistUser("novica@gmail.com", "DRIVER");
        Driver newDriver = createAndPersistDriver(secondDriverUser);
        entityManager.flush();

        List<Ride> results = rideRepository.findByDriverId(newDriver.getId());

        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("findByDriverId should return all rides for a driver with history")
    void testFindByDriverId_LargeDataset() {
        int rideCount = 6;
        for (int i = 0; i < rideCount; i++) {
            createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        }

        User otherUser = createAndPersistUser("otherdriver@gmail.com", "DRIVER");
        Driver otherDriver = createAndPersistDriver(otherUser);
        createAndPersistRide(otherDriver, passenger, RideStatus.FINISHED);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByDriverId(driver.getId());

        assertThat(results).hasSize(rideCount);

        assertThat(results).allSatisfy(ride ->
                assertThat(ride.getDriver().getId()).isEqualTo(driver.getId())
        );
    }

    // 3. findByStatus
    @Test
    void testFindByStatus() {
        createAndPersistRide(driver, passenger, RideStatus.STARTED);
        createAndPersistRide(null, passenger, RideStatus.ONGOING);

        assertThat(rideRepository.findByStatus(RideStatus.STARTED)).hasSize(1);
        assertThat(rideRepository.findByStatus(RideStatus.STOPPED)).isEmpty();
    }

    @Test
    @DisplayName("findByStatus: Should return multiple rides when all match the given status")
    void testFindByStatus_MultipleMatches() {
        createAndPersistRide(driver, passenger, RideStatus.ONGOING);
        createAndPersistRide(driver, passenger, RideStatus.ONGOING);
        createAndPersistRide(driver, passenger, RideStatus.ONGOING);

        createAndPersistRide(driver, passenger, RideStatus.USER_CANCELLED);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByStatus(RideStatus.ONGOING);

        assertThat(results).hasSize(3);
        assertThat(results).allMatch(ride -> ride.getStatus() == RideStatus.ONGOING);
    }

    @Test
    @DisplayName("findByStatus should distinguish between different cancellation statuses")
    void testFindByStatus_EnumSafety() {
        createAndPersistRide(driver, passenger, RideStatus.USER_CANCELLED);
        createAndPersistRide(driver, passenger, RideStatus.DRIVER_CANCELLED);

        createAndPersistRide(driver, passenger, RideStatus.FINISHED);

        entityManager.flush();
        entityManager.clear();

        List<Ride> userCancelled = rideRepository.findByStatus(RideStatus.USER_CANCELLED);

        List<Ride> driverCancelled = rideRepository.findByStatus(RideStatus.DRIVER_CANCELLED);

        assertThat(userCancelled).hasSize(1);
        assertThat(userCancelled.getFirst().getStatus()).isEqualTo(RideStatus.USER_CANCELLED);

        assertThat(driverCancelled).hasSize(1);
        assertThat(driverCancelled.getFirst().getStatus()).isEqualTo(RideStatus.DRIVER_CANCELLED);
    }



    // 4. findByStatusIn
    @Test
    void testFindByStatusIn() {
        createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        createAndPersistRide(driver, passenger, RideStatus.USER_CANCELLED);

        List<RideStatus> statuses = List.of(RideStatus.FINISHED, RideStatus.USER_CANCELLED);
        assertThat(rideRepository.findByStatusIn(statuses)).hasSize(2);
        assertThat(rideRepository.findByStatusIn(List.of(RideStatus.STARTED))).isEmpty();
    }

    @Test
    @DisplayName("findByStatusIn should return empty list")
    void testFindByStatusIn_EmptyParameter() {
        createAndPersistRide(driver, passenger, RideStatus.ONGOING);

        List<RideStatus> emptyStatuses = List.of();
        List<Ride> results = rideRepository.findByStatusIn(emptyStatuses);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("findByStatusIn should return only matching when some statuses missing from db")
    void testFindByStatusIn_PartialMatch() {
        createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        createAndPersistRide(driver, passenger, RideStatus.ONGOING);

        createAndPersistRide(driver, passenger, RideStatus.STARTED);

        entityManager.flush();
        entityManager.clear();

        List<RideStatus> searchStatuses = List.of(RideStatus.FINISHED, RideStatus.USER_CANCELLED);
        List<Ride> results = rideRepository.findByStatusIn(searchStatuses);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(ride -> ride.getStatus() == RideStatus.FINISHED);
    }

    // 5. getRideById
    @Test
    void testGetRideById() {
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.ONGOING);

        assertThat(rideRepository.getRideById(ride.getId())).isPresent();
        assertThat(rideRepository.getRideById(999L)).isEmpty();
    }

    @Test
    @DisplayName("getRideById should return empty after the ride is deleted")
    void testGetRideById_DeletedRecord() {
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.ONGOING);
        long rideId = ride.getId();

        Ride toDelete = entityManager.find(Ride.class, rideId);
        entityManager.remove(toDelete);
        entityManager.flush();
        entityManager.clear();

        Optional<Ride> result = rideRepository.getRideById(rideId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getRideById should correctly load nested RideRequest and its properties")
    void testGetRideById_RelationshipLoading() {
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.ONGOING);
        long rideId = ride.getId();

        entityManager.flush();
        entityManager.clear();

        Optional<Ride> result = rideRepository.getRideById(rideId);

        assertThat(result).isPresent();
        Ride foundRide = result.get();

        assertThat(foundRide.getRideRequest()).isNotNull();

        assertThat(foundRide.getRideRequest().getCreator().getEmail()).isEqualTo("passenger@gmail.com");

        assertThat(foundRide.getRideRequest().getVehicleType()).isNotNull();
        assertThat(foundRide.getRideRequest().getVehicleType().getPrice()).isEqualTo(15.0);
    }

    // 6. findByRideRequest_Creator_Id
    @Test
    void testFindByRideRequestCreatorId() {
        createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        User otherPassenger = createAndPersistUser("otherpassenger@gmail.com", "USER");

        assertThat(rideRepository.findByRideRequest_Creator_Id(passenger.getId())).hasSize(1);
        assertThat(rideRepository.findByRideRequest_Creator_Id(otherPassenger.getId())).isEmpty();
    }

    @Test
    @DisplayName("findByRideRequest_Creator_Id should isolate rides between different passengers")
    void testFindByRideRequest_Creator_Id_Isolation() {
        User passengerA = passenger;
        User passengerB = createAndPersistUser("passengerB@gmail.com", "USER");

        createAndPersistRide(driver, passengerA, RideStatus.FINISHED);
        createAndPersistRide(driver, passengerB, RideStatus.FINISHED);
        createAndPersistRide(driver, passengerB, RideStatus.FINISHED);

        entityManager.flush();
        entityManager.clear();

        List<Ride> ridesA = rideRepository.findByRideRequest_Creator_Id(passengerA.getId());

        assertThat(ridesA).hasSize(1);
        assertThat(ridesA.getFirst().getRideRequest().getCreator().getId()).isEqualTo(passengerA.getId());
    }

    @Test
    @DisplayName("findByRideRequest_Creator_Id should only return rides where User is the creator, not the driver")
    void testFindByRideRequest_Creator_Id_DualRoles() {
        User userX = driverUser;

        User userY = passenger;
        createAndPersistRide(driver, userY, RideStatus.FINISHED);

        User otherDriverUser = createAndPersistUser("otherdriver1@gmail.com", "DRIVER");
        Driver otherDriver = createAndPersistDriver(otherDriverUser);
        createAndPersistRide(otherDriver, userX, RideStatus.FINISHED);

        entityManager.flush();
        entityManager.clear();

        List<Ride> createdByX = rideRepository.findByRideRequest_Creator_Id(userX.getId());

        assertThat(createdByX).hasSize(1);
        assertThat(createdByX.getFirst().getRideRequest().getCreator().getId()).isEqualTo(userX.getId());
        assertThat(createdByX.getFirst().getDriver().getId()).isEqualTo(otherDriver.getId());
    }

    // 7. findByDriver_User_Id
    @Test
    void testFindByDriverUserId() {
        createAndPersistRide(driver, passenger, RideStatus.STARTED);

        assertThat(rideRepository.findByDriver_User_Id(driverUser.getId())).hasSize(1);
        assertThat(rideRepository.findByDriver_User_Id(999L)).isEmpty();
    }

    @Test
    @DisplayName("findByDriver_User_Id should return empty list when user id belongs to not driver")
    void testFindByDriver_User_Id_NonDriverUser() {
        long nonDriverUserId = passenger.getId();

        createAndPersistRide(driver, passenger, RideStatus.ONGOING);
        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByDriver_User_Id(nonDriverUserId);

        assertThat(results).isEmpty();
    }

    // 8. findByRideRequest_Creator_IdAndStatus
    @Test
    void testFindByRideRequestCreatorIdAndStatus() {
        createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        createAndPersistRide(driver, passenger, RideStatus.STARTED);

        assertThat(rideRepository.findByRideRequest_Creator_IdAndStatus(passenger.getId(), RideStatus.FINISHED)).hasSize(1);
        assertThat(rideRepository.findByRideRequest_Creator_IdAndStatus(passenger.getId(), RideStatus.USER_CANCELLED)).isEmpty();
    }

    @Test
    @DisplayName("findByRideRequest_Creator_IdAndStatus should return empty when creator has rides but none with matching status")
    void testFindByRideRequest_Creator_IdAndStatus_StatusMismatch() {
        createAndPersistRide(driver, passenger, RideStatus.FINISHED);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatus(
                passenger.getId(),
                RideStatus.STARTED
        );

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("findByRideRequest_Creator_IdAndStatus should return empty when status exists but for a different creator")
    void testFindByRideRequest_Creator_IdAndStatus_CreatorMismatch() {
        createAndPersistRide(driver, passenger, RideStatus.FINISHED);

        User passengerB = createAndPersistUser("passengerB@gmail.com", "USER");

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatus(
                passengerB.getId(),
                RideStatus.FINISHED
        );

        assertThat(results).isEmpty();
    }

    // 9. findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween
    @Test
    void testFindByCreatorStatusAndDateRange() {
        LocalDateTime now = LocalDateTime.now();
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        ride.setFinishedAt(now.minusDays(1));
        entityManager.persist(ride);

        var result = rideRepository.findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween(
                passenger.getId(), RideStatus.FINISHED, now.minusDays(2), now);

        assertThat(result).hasSize(1);

        var resultEmpty = rideRepository.findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween(
                passenger.getId(), RideStatus.FINISHED, now.plusDays(1), now.plusDays(2));
        assertThat(resultEmpty).isEmpty();
    }

    @Test
    @DisplayName("findByCreatorStatusAndDateRange should include ride when finishedAt is exactly as from")
    void testFindByCreatorStatusAndDateRange_InclusiveStart() {
        LocalDateTime boundaryTime = LocalDateTime.of(2026, 2, 13, 12, 0, 0);
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        ride.setFinishedAt(boundaryTime);
        entityManager.persist(ride);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween(
                passenger.getId(),
                RideStatus.FINISHED,
                boundaryTime,
                boundaryTime.plusHours(1)
        );

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getFinishedAt()).isEqualTo(boundaryTime);
    }

    @Test
    @DisplayName("findByCreatorStatusAndDateRange should include ride when finishedAt is exactly to")
    void testFindByCreatorStatusAndDateRange_InclusiveEnd() {
        LocalDateTime boundaryTime = LocalDateTime.of(2026, 2, 13, 15, 0, 0);
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        ride.setFinishedAt(boundaryTime);
        entityManager.persist(ride);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByRideRequest_Creator_IdAndStatusAndFinishedAtBetween(
                passenger.getId(),
                RideStatus.FINISHED,
                boundaryTime.minusHours(1),
                boundaryTime
        );

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getFinishedAt()).isEqualTo(boundaryTime);
    }

    // 10. findByDriver_IdAndStatusAndFinishedAtBetween
    @Test
    void testFindByDriverStatusAndDateRange() {
        LocalDateTime now = LocalDateTime.now();
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        ride.setFinishedAt(now.minusHours(5));
        entityManager.persist(ride);

        assertThat(rideRepository.findByDriver_IdAndStatusAndFinishedAtBetween(
                driver.getId(), RideStatus.FINISHED, now.minusHours(10), now)).hasSize(1);

        assertThat(rideRepository.findByDriver_IdAndStatusAndFinishedAtBetween(
                driver.getId(), RideStatus.STARTED, now.minusHours(10), now)).isEmpty();
    }


    @Test
    @DisplayName("findByDriver_IdAndStatusAndFinishedAtBetween should filter out rides outside the date range")
    void testFindByDriver_IdAndStatusAndFinishedAtBetween_RangeIsolation() {
        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime yesterday = today.minusDays(1);

        Ride rideYesterday = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        rideYesterday.setFinishedAt(yesterday);
        entityManager.persist(rideYesterday);

        Ride rideToday = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        rideToday.setFinishedAt(today);
        entityManager.persist(rideToday);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByDriver_IdAndStatusAndFinishedAtBetween(
                driver.getId(),
                RideStatus.FINISHED,
                today.minusHours(2),
                today.plusHours(2)
        );

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getFinishedAt()).isEqualTo(today);
    }

    @Test
    @DisplayName("findByDriver_IdAndStatusAndFinishedAtBetween should ignore rides where finishedAt is null")
    void testFindByDriver_IdAndStatusAndFinishedAtBetween_NullDate() {
        Ride ongoingRide = createAndPersistRide(driver, passenger, RideStatus.STARTED);
        ongoingRide.setFinishedAt(null);
        entityManager.persist(ongoingRide);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByDriver_IdAndStatusAndFinishedAtBetween(
                driver.getId(),
                RideStatus.FINISHED,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(10)
        );

        assertThat(results).isEmpty();
    }

    // 11. findByStatusAndFinishedAtBetween
    @Test
    void testFindByStatusAndDateRange() {
        LocalDateTime now = LocalDateTime.now();
        Ride ride = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        ride.setFinishedAt(now.minusMinutes(30));
        entityManager.persist(ride);

        assertThat(rideRepository.findByStatusAndFinishedAtBetween(
                RideStatus.FINISHED, now.minusHours(1), now)).hasSize(1);

        assertThat(rideRepository.findByStatusAndFinishedAtBetween(
                RideStatus.FINISHED, now.minusMinutes(35), now)).hasSize(1);
    }

    @Test
    @DisplayName("findByStatusAndFinishedAtBetween should only return matching status within the same time")
    void testFindByStatusAndFinishedAtBetween_StatusIsolation() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Ride finishedRide = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        finishedRide.setFinishedAt(now);
        entityManager.persist(finishedRide);

        Ride canceledRide = createAndPersistRide(driver, passenger, RideStatus.USER_CANCELLED);
        canceledRide.setFinishedAt(now);
        entityManager.persist(canceledRide);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByStatusAndFinishedAtBetween(
                RideStatus.FINISHED,
                now.minusMinutes(30),
                now.plusMinutes(30)
        );

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getStatus()).isEqualTo(RideStatus.FINISHED);
    }

    @Test
    @DisplayName("findByStatusAndFinishedAtBetween: Should correctly match rides with 10 year difference")
    void testFindByStatusAndFinishedAtBetween_GlobalRange() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime tenYearsAgo = now.minusYears(10);

        Ride oldRide = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        oldRide.setFinishedAt(tenYearsAgo);
        entityManager.persist(oldRide);

        Ride recentRide = createAndPersistRide(driver, passenger, RideStatus.FINISHED);
        recentRide.setFinishedAt(now);
        entityManager.persist(recentRide);

        entityManager.flush();
        entityManager.clear();

        List<Ride> results = rideRepository.findByStatusAndFinishedAtBetween(
                RideStatus.FINISHED,
                now.minusYears(11),
                now.plusYears(1)
        );

        assertThat(results).hasSize(2);
    }

    // helpers

    private Ride createAndPersistRide(Driver d, User p, RideStatus status) {
        RideRoute route = new RideRoute();
        entityManager.persist(route);

        VehicleType vType = new VehicleType();
        vType.setType("TYPE_" + java.util.UUID.randomUUID().toString().substring(0, 8));
        vType.setPrice(15.0);
        entityManager.persist(vType);

        RideRequest req = new RideRequest();
        req.setCreator(p);
        req.setRideRoute(route);
        req.setVehicleType(vType);
        req.setStatus(RideRequestStatus.ACCEPTED);
        req.setScheduleType(ScheduleType.NOW);
        entityManager.persist(req);

        Ride ride = new Ride();
        ride.setDriver(d);
        ride.setRideRequest(req);
        ride.setStatus(status);
        return entityManager.persistFlushFind(ride);
    }

    private User createAndPersistUser(String email, String role) {
        Person person = new Person();
        person.setFirstName("Test"); person.setLastName("User");
        person.setPhoneNumber("000"); person.setAddress("Addr");
        person.setBirthDate(LocalDateTime.now());
        entityManager.persist(person);

        UserRole userRole;
        try {
            userRole = entityManager.getEntityManager()
                    .createQuery("SELECT r FROM UserRole r WHERE r.roleName = :name", UserRole.class)
                    .setParameter("name", role)
                    .getSingleResult();
        } catch (Exception e) {
            userRole = new UserRole();
            userRole.setRoleName(role);
            entityManager.persist(userRole);
        }

        User user = new User();
        user.setEmail(email);
        user.setPerson(person);
        user.setUserRole(userRole);
        user.setUserStatus(UserStatus.ACTIVE);
        return entityManager.persist(user);
    }

    private Driver createAndPersistDriver(User user) {
        Driver d = new Driver();
        d.setUser(user);
        d.setStatus(DriverStatus.AVAILABLE);
        return entityManager.persist(d);
    }
}