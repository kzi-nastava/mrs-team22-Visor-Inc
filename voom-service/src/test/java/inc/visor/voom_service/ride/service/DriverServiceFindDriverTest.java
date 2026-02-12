package inc.visor.voom_service.ride.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverState;
import inc.visor.voom_service.driver.model.DriverStateChange;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.driver.repository.DriverVehicleChangeRequestRepository;
import inc.visor.voom_service.driver.service.DriverActivityService;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class DriverServiceFindDriverTest {

    @InjectMocks
    private DriverService driverService;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @Mock
    private RideService rideService;

    @Mock
    private DriverActivityService driverActivityService;

    @Mock
    private DriverVehicleChangeRequestRepository changeRequestRepository;

    @BeforeEach
    void setup() {
        driverService = spy(driverService);
    }

    @Test
    @DisplayName("01 - Should return nearest AVAILABLE driver for NOW ride")
    void shouldReturnNearestAvailableDriverForNowRide() {

        VehicleType vehicleType = buildVehicleType(1L);

        RideRequest rideRequest = buildValidRideRequest(
                ScheduleType.NOW,
                vehicleType,
                false,
                false,
                0
        );

        Driver driver = buildDriver(
                10L,
                DriverStatus.AVAILABLE,
                UserStatus.ACTIVE
        );

        Vehicle vehicle = buildVehicle(
                driver,
                vehicleType,
                false,
                false,
                4
        );

        List<RideRequestCreateDto.DriverLocationDto> snapshot = List.of(
                loc(10L, 45.0, 19.0)
        );

        when(driverRepository.findById(10L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(10L))
                .thenReturn(Optional.of(vehicle));

        mockDriverActive(10L);

        doReturn(2.0)
                .when(driverService)
                .calculateActiveHoursLast24h(10L);

        when(rideService.isDriverFreeForRide(driver, rideRequest))
                .thenReturn(true);

        Driver result = driverService.findDriverForRideRequest(
                rideRequest,
                snapshot
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("02 - Should return driver for SCHEDULED ride even if driver is BUSY")
    void shouldReturnDriverForScheduledRideEvenIfBusy() {

        VehicleType vehicleType = buildVehicleType(1L);

        RideRequest rideRequest = buildValidRideRequest(
                ScheduleType.LATER,
                vehicleType,
                false,
                false,
                0
        );

        Driver driver = buildDriver(
                20L,
                DriverStatus.BUSY,
                UserStatus.ACTIVE
        );

        Vehicle vehicle = buildVehicle(
                driver,
                vehicleType,
                false,
                false,
                4
        );

        List<RideRequestCreateDto.DriverLocationDto> snapshot = List.of(
                loc(20L, 45.0, 19.0)
        );

        when(driverRepository.findById(20L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(20L))
                .thenReturn(Optional.of(vehicle));

        mockDriverActive(20L);

        doReturn(3.0)
                .when(driverService)
                .calculateActiveHoursLast24h(20L);

        when(rideService.isDriverFreeForRide(driver, rideRequest))
                .thenReturn(true);

        Driver result = driverService.findDriverForRideRequest(
                rideRequest,
                snapshot
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(20L, result.getId());
    }

    @Test
    @DisplayName("03 - Should return null for NOW ride when only driver is BUSY")
    void shouldReturnNullForNowRideWhenDriverBusy() {

        VehicleType vehicleType = buildVehicleType(1L);

        RideRequest rideRequest = buildValidRideRequest(
                ScheduleType.NOW,
                vehicleType,
                false,
                false,
                0
        );

        Driver driver = buildDriver(
                30L,
                DriverStatus.BUSY,
                UserStatus.ACTIVE
        );

        Vehicle vehicle = buildVehicle(
                driver,
                vehicleType,
                false,
                false,
                4
        );

        List<RideRequestCreateDto.DriverLocationDto> snapshot = List.of(
                loc(30L, 45.0, 19.0)
        );

        when(driverRepository.findById(30L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(30L))
                .thenReturn(Optional.of(vehicle));

        mockDriverActive(30L);

        doReturn(1.0)
                .when(driverService)
                .calculateActiveHoursLast24h(30L);

        Driver result = driverService.findDriverForRideRequest(
                rideRequest,
                snapshot
        );

        assertNull(result);
    }

    @Test
    @DisplayName("04 - Should return finishingSoon driver for SCHEDULED ride")
    void shouldReturnFinishingSoonDriverForNowRide() {

        VehicleType vehicleType = buildVehicleType(1L);

        RideRequest rideRequest = buildValidRideRequest(
                ScheduleType.LATER,
                vehicleType,
                false,
                false,
                0
        );

        Driver driver = buildDriver(
                40L,
                DriverStatus.BUSY,
                UserStatus.ACTIVE
        );

        Vehicle vehicle = buildVehicle(
                driver,
                vehicleType,
                false,
                false,
                4
        );

        List<RideRequestCreateDto.DriverLocationDto> snapshot = List.of(
                loc(40L, 45.0, 19.0)
        );

        when(driverRepository.findById(40L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(40L))
                .thenReturn(Optional.of(vehicle));

        mockDriverActive(40L);

        doReturn(2.0)
                .when(driverService)
                .calculateActiveHoursLast24h(40L);

        when(rideService.isDriverFreeForRide(driver, rideRequest))
                .thenReturn(false);

        doReturn(true)
                .when(driverService)
                .finishesInNext10Minutes(driver);

        Driver result = driverService.findDriverForRideRequest(
                rideRequest,
                snapshot
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(40L, result.getId());
    }

    @Test
    @DisplayName("05 - Should return null when driver worked more than 8h in last 24h")
    void shouldReturnNullWhenDriverOverworked() {

        VehicleType vehicleType = buildVehicleType(1L);

        RideRequest rideRequest = buildValidRideRequest(
                ScheduleType.NOW,
                vehicleType,
                false,
                false,
                0
        );

        Driver driver = buildDriver(
                50L,
                DriverStatus.AVAILABLE,
                UserStatus.ACTIVE
        );

        Vehicle vehicle = buildVehicle(
                driver,
                vehicleType,
                false,
                false,
                4
        );

        List<RideRequestCreateDto.DriverLocationDto> snapshot = List.of(
                loc(50L, 45.0, 19.0)
        );

        when(driverRepository.findById(50L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(50L))
                .thenReturn(Optional.of(vehicle));

        mockDriverActive(50L);

        doReturn(8.0)
                .when(driverService)
                .calculateActiveHoursLast24h(50L);

        Driver result = driverService.findDriverForRideRequest(
                rideRequest,
                snapshot
        );

        assertNull(result);
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("vehicleMismatchProvider")
    @DisplayName("06 - Should return null when vehicle does not match ride requirements")
    void shouldReturnNullWhenVehicleDoesNotMatch(
            String scenario,
            VehicleType vehicleType,
            VehicleType requestVehicleType,
            boolean vehicleBaby,
            boolean vehiclePet,
            int vehicleSeats,
            boolean requestBaby,
            boolean requestPet,
            int linkedPassengers
    ) {

        RideRequest rideRequest = buildValidRideRequest(
                ScheduleType.NOW,
                requestVehicleType,
                requestBaby,
                requestPet,
                linkedPassengers
        );

        Driver driver = buildDriver(
                60L,
                DriverStatus.AVAILABLE,
                UserStatus.ACTIVE
        );

        Vehicle vehicle = buildVehicle(
                driver,
                vehicleType,
                vehicleBaby,
                vehiclePet,
                vehicleSeats
        );

        List<RideRequestCreateDto.DriverLocationDto> snapshot = List.of(
                loc(60L, 45.0, 19.0)
        );

        when(driverRepository.findById(60L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(60L))
                .thenReturn(Optional.of(vehicle));

        mockDriverActive(60L);

        Driver result = driverService.findDriverForRideRequest(
                rideRequest,
                snapshot
        );

        assertNull(result);
    }

    static Stream<Arguments> vehicleMismatchProvider() {

        VehicleType standard = new VehicleType();
        standard.setId(1L);
        standard.setType("STANDARD");

        VehicleType luxury = new VehicleType();
        luxury.setId(2L);
        luxury.setType("LUXURY");

        return Stream.of(
                // Vehicle type mismatch
                Arguments.of(
                        "Vehicle type mismatch",
                        standard,
                        luxury,
                        false,
                        false,
                        4,
                        false,
                        false,
                        0
                ),
                // Pet required but vehicle not petFriendly
                Arguments.of(
                        "Pet required but vehicle not pet friendly",
                        standard,
                        standard,
                        false,
                        false,
                        4,
                        false,
                        true,
                        0
                ),
                // Baby seat required but vehicle doesnt have it
                Arguments.of(
                        "Baby seat required but vehicle doesn't have it",
                        standard,
                        standard,
                        false,
                        false,
                        4,
                        true,
                        false,
                        0
                ),
                // Not enough seats
                Arguments.of(
                        "Not enough seats",
                        standard,
                        standard,
                        false,
                        false,
                        2,
                        false,
                        false,
                        2
                )
        );
    }

    protected RideRequest buildValidRideRequest(
            ScheduleType scheduleType,
            VehicleType vehicleType,
            boolean baby,
            boolean pets,
            int linkedPassengersCount
    ) {
        RideRequest req = new RideRequest();
        req.setScheduleType(scheduleType);
        req.setVehicleType(vehicleType);
        req.setBabyTransport(baby);
        req.setPetTransport(pets);
        req.setLinkedPassengerEmails(
                IntStream.range(0, linkedPassengersCount)
                        .mapToObj(i -> "passenger" + i + "@mail.com")
                        .toList()
        );

        req.setRideRoute(buildRoute());
        return req;
    }

    protected RideRoute buildRoute() {
        RoutePoint pickup = new RoutePoint();
        pickup.setLatitude(45.0);
        pickup.setLongitude(19.0);
        pickup.setOrderIndex(0);
        pickup.setPointType(RoutePointType.PICKUP);

        RoutePoint dropoff = new RoutePoint();
        dropoff.setLatitude(45.1);
        dropoff.setLongitude(19.1);
        dropoff.setOrderIndex(1);
        dropoff.setPointType(RoutePointType.DROPOFF);

        RideRoute route = new RideRoute();
        route.setRoutePoints(List.of(pickup, dropoff));
        return route;
    }

    protected Driver buildDriver(
            Long id,
            DriverStatus status,
            UserStatus userStatus
    ) {
        Person p = new Person();
        p.setFirstName("Nikola");
        p.setLastName("Bjelica");

        User user = new User();
        user.setUserStatus(userStatus);
        user.setPerson(p);

        Driver d = new Driver();
        d.setId(id);
        d.setStatus(status);
        d.setUser(user);
        return d;
    }

    protected Vehicle buildVehicle(
            Driver driver,
            VehicleType type,
            boolean baby,
            boolean pets,
            int seats
    ) {
        Vehicle v = new Vehicle();
        v.setDriver(driver);
        v.setVehicleType(type);
        v.setBabySeat(baby);
        v.setPetFriendly(pets);
        v.setNumberOfSeats(seats);
        return v;
    }

    protected VehicleType buildVehicleType(Long id) {
        VehicleType vt = new VehicleType();
        vt.setId(id);
        vt.setType("STANDARD");
        return vt;
    }

    protected RideRequestCreateDto.DriverLocationDto loc(
            Long driverId,
            double lat,
            double lng
    ) {
        RideRequestCreateDto.DriverLocationDto dto
                = new RideRequestCreateDto.DriverLocationDto();
        dto.driverId = driverId;
        dto.lat = lat;
        dto.lng = lng;
        return dto;
    }

    protected void mockDriverActive(Long driverId) {
        DriverStateChange change = new DriverStateChange();
        change.setCurrentState(DriverState.ACTIVE);
        change.setPerformedAt(LocalDateTime.now().minusMinutes(5));

        when(driverActivityService.getLastStateChange(driverId))
                .thenReturn(Optional.of(change));
    }

    protected void mockDriverInactive(Long driverId) {
        when(driverActivityService.getLastStateChange(driverId))
                .thenReturn(Optional.empty());
    }

    protected void mockDriverWorkedHours(Long driverId, double hours) {
        when(driverService.calculateActiveHoursLast24h(driverId))
                .thenReturn(hours);
    }

}
