package inc.visor.voom_service.ride.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.InvalidRouteOrderException;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideEstimationResult;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.repository.RideRequestRepository;
import inc.visor.voom_service.shared.notification.model.enums.NotificationType;
import inc.visor.voom_service.shared.notification.service.NotificationService;
import inc.visor.voom_service.simulation.Simulator;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.service.VehicleTypeService;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class RideRequestServiceTest {

    @InjectMocks
    private RideRequestService rideRequestService;

    @Mock
    private RideRequestRepository rideRequestRepository;

    @Mock
    private RideEstimateService rideEstimationService;

    @Mock
    private DriverService driverService;

    @Mock
    private RideWsService rideWsService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Simulator simulator;

    @Mock
    private UserService userService;

    @Mock
    private VehicleTypeService vehicleTypeService;

    @Mock
    private RideService rideService;

    @Mock
    private RideRepository rideRepository;

    @Test
    @Order(1)
    @DisplayName("01 - Should successfully create NOW ride and assign driver")
    void shouldCreateNowRideSuccessfully() {
        Long userId = 1L;

        User user = buildActiveUser(userId);
        VehicleType vehicleType = buildVehicleType(1L);
        Driver driver = buildAvailableDriver(10L);
        RideRequestCreateDto dto = buildValidRequest();

        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(vehicleTypeService.getVehicleType(1L))
                .thenReturn(Optional.of(vehicleType));
        when(rideEstimationService.estimate(
                eq(dto.route.points),
                eq(vehicleType)
        )).thenReturn(new RideEstimationResult(10.0, 12.5));

        when(driverService.findDriverForRideRequest(
                any(),
                eq(dto.getFreeDriversSnapshot())
        )).thenReturn(driver);

        when(driverService.updateDriver(driver)).thenReturn(driver);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);

        RideRequestResponseDto response
                = rideRequestService.createRideRequest(dto, userId);

        assertNotNull(response);
        assertEquals(RideRequestStatus.ACCEPTED, response.getStatus());
        assertNotNull(response.getDriver());

        assertEquals(DriverStatus.BUSY, driver.getStatus());

        verify(rideRequestRepository, times(1)).save(any());
        verify(rideService, times(1)).save(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();
        assertNotNull(savedRide);
        assertEquals(ScheduleType.NOW, savedRide.getRideRequest().getScheduleType());
        assertEquals(10.0, response.getDistanceKm());
        assertEquals(12.5, response.getPrice());
        assertEquals("Nikola", response.getDriver().getFirstName());
        assertEquals("Bjelica", response.getDriver().getLastName());
        assertEquals(driver, savedRide.getDriver());
        assertEquals(ScheduleType.NOW, savedRide.getRideRequest().getScheduleType());
        assertEquals(savedRide.getRideRequest().getStatus(), RideRequestStatus.ACCEPTED);
        assertEquals(savedRide.getStatus(), inc.visor.voom_service.ride.model.enums.RideStatus.ONGOING);

        verify(notificationService, times(2))
                .createAndSendNotification(any(), any(), any(), any(), any());

        verify(rideWsService, times(1)).sendDriverAssigned(any());

        verify(simulator, times(1))
                .changeDriverRoute(eq(10L), anyDouble(), anyDouble());
    }

    @Test
    @Order(2)
    @DisplayName("02 - Should successfully create SCHEDULED ride and not trigger WS or simulator")
    void shouldCreateScheduledRideSuccessfully() {

        Long userId = 1L;

        User user = buildActiveUser(userId);
        VehicleType vehicleType = buildVehicleType(1L);
        Driver driver = buildAvailableDriver(10L);

        RideRequestCreateDto dto = buildValidRequest();

        dto.schedule.type = "LATER";
        dto.schedule.startAt = Instant.now().plusSeconds(3600);

        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(vehicleTypeService.getVehicleType(1L))
                .thenReturn(Optional.of(vehicleType));

        when(rideEstimationService.estimate(
                ArgumentMatchers.argThat(points
                        -> points != null
                && points.size() == 2
                ),
                eq(vehicleType)
        )).thenReturn(new RideEstimationResult(20.0, 30.0));

        when(driverService.findDriverForRideRequest(
                any(),
                eq(dto.getFreeDriversSnapshot())
        )).thenReturn(driver);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);

        RideRequestResponseDto response
                = rideRequestService.createRideRequest(dto, userId);

        assertNotNull(response);
        assertEquals(RideRequestStatus.ACCEPTED, response.getStatus());
        assertNotNull(response.getDriver());

        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());

        verify(rideRequestRepository, times(1)).save(any());
        verify(rideService, times(1)).save(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();

        assertEquals(ScheduleType.LATER, savedRide.getRideRequest().getScheduleType());
        assertEquals(RideRequestStatus.ACCEPTED, savedRide.getRideRequest().getStatus());

        assertEquals(
                inc.visor.voom_service.ride.model.enums.RideStatus.SCHEDULED,
                savedRide.getStatus()
        );

        assertEquals(30.0, response.getPrice());
        assertEquals(20.0, response.getDistanceKm());

        assertEquals("Nikola", response.getDriver().getFirstName());
        assertEquals("Bjelica", response.getDriver().getLastName());

        verify(notificationService, times(1))
                .createAndSendNotification(any(), any(), any(), any(), any());

        verify(rideWsService, times(0)).sendDriverAssigned(any());

        verify(simulator, times(0))
                .changeDriverRoute(anyLong(), anyDouble(), anyDouble());
    }

    @Test
    @Order(3)
    @DisplayName("03 - Should throw NotFoundException when user not found")
    void shouldThrowWhenUserNotFound() {

        Long userId = 1L;
        RideRequestCreateDto dto = buildValidRequest();

        when(userService.getUser(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> rideRequestService.createRideRequest(dto, userId)
        );

        verify(rideRequestRepository, times(0)).save(any());
        verify(rideService, times(0)).save(any());
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
    }

    @Test
    @Order(4)
    @DisplayName("04 - Should throw NotFoundException when vehicle type not found")
    void shouldThrowWhenVehicleTypeNotFound() {

        Long userId = 1L;
        RideRequestCreateDto dto = buildValidRequest();

        when(userService.getUser(userId))
                .thenReturn(Optional.of(buildActiveUser(userId)));

        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> rideRequestService.createRideRequest(dto, userId)
        );

        verify(rideRequestRepository, times(0)).save(any());
        verify(rideService, times(0)).save(any());
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
    }

    @Test
    @Order(5)
    @DisplayName("05 - Should throw InvalidRouteOrderException when route order invalid")
    void shouldThrowWhenRouteOrderInvalid() {

        Long userId = 1L;
        RideRequestCreateDto dto = buildValidRequest();

        dto.route.points.get(0).orderIndex = 3;

        when(userService.getUser(userId))
                .thenReturn(Optional.of(buildActiveUser(userId)));

        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(buildVehicleType(1L)));

        when(rideEstimationService.estimate(any(), any()))
                .thenReturn(new RideEstimationResult(10.0, 12.0));

        assertThrows(InvalidRouteOrderException.class,
                () -> rideRequestService.createRideRequest(dto, userId)
        );

        verify(rideRequestRepository, times(0)).save(any());
        verify(rideService, times(0)).save(any());
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
    }

    @Test
    @Order(6)
    @DisplayName("06 - Should reject NOW ride when no driver found")
    void shouldRejectNowRideWhenNoDriverFound() {

        Long userId = 1L;

        User user = buildActiveUser(userId);
        VehicleType vehicleType = buildVehicleType(1L);

        RideRequestCreateDto dto = buildValidRequest();

        when(userService.getUser(userId))
                .thenReturn(Optional.of(user));

        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(vehicleType));

        when(rideEstimationService.estimate(
                eq(dto.route.points),
                eq(vehicleType)
        )).thenReturn(new RideEstimationResult(15.0, 25.0));

        when(driverService.findDriverForRideRequest(any(), any()))
                .thenReturn(null);

        RideRequestResponseDto response
                = rideRequestService.createRideRequest(dto, userId);

        assertNotNull(response);
        assertEquals(RideRequestStatus.REJECTED, response.getStatus());
        assertEquals(25.0, response.getPrice());
        assertEquals(15.0, response.getDistanceKm());
        assertEquals(null, response.getDriver());

        verify(rideRequestRepository, times(1)).save(any());
        verify(rideService, times(0)).save(any());

        verifyNoInteractions(notificationService);
        verifyNoInteractions(rideWsService);
        verifyNoInteractions(simulator);
    }

    @Test
    @Order(7)
    @DisplayName("07 - Should reject SCHEDULED ride when no driver found")
    void shouldRejectScheduledRideWhenNoDriverFound() {

        Long userId = 1L;

        User user = buildActiveUser(userId);
        VehicleType vehicleType = buildVehicleType(1L);

        RideRequestCreateDto dto = buildValidRequest();
        dto.schedule.type = "LATER";
        dto.schedule.startAt = Instant.now().plusSeconds(3600);

        when(userService.getUser(userId))
                .thenReturn(Optional.of(user));

        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(vehicleType));

        when(rideEstimationService.estimate(
                eq(dto.route.points),
                eq(vehicleType)
        )).thenReturn(new RideEstimationResult(50.0, 70.0));

        when(driverService.findDriverForRideRequest(any(), any()))
                .thenReturn(null);

        RideRequestResponseDto response
                = rideRequestService.createRideRequest(dto, userId);

        assertNotNull(response);
        assertEquals(RideRequestStatus.REJECTED, response.getStatus());
        assertEquals(70.0, response.getPrice());
        assertEquals(50.0, response.getDistanceKm());
        assertEquals(null, response.getDriver());

        verify(rideRequestRepository, times(1)).save(any());

        verify(rideService, times(0)).save(any());

        verifyNoInteractions(notificationService);
        verifyNoInteractions(rideWsService);
        verifyNoInteractions(simulator);
    }

    @Test
    @Order(8)
    @DisplayName("08 - Should throw AccessDeniedException when user is suspended")
    void shouldThrowWhenUserSuspended() {

        Long userId = 1L;

        RideRequestCreateDto dto = buildValidRequest();

        User suspendedUser = buildActiveUser(userId);
        suspendedUser.setUserStatus(UserStatus.SUSPENDED);

        when(userService.getUser(userId))
                .thenReturn(Optional.of(suspendedUser));

        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(buildVehicleType(1L)));

        when(rideEstimationService.estimate(
                eq(dto.route.points),
                any()
        )).thenReturn(new RideEstimationResult(10.0, 12.0));

        assertThrows(
                inc.visor.voom_service.exception.AccessDeniedException.class,
                () -> rideRequestService.createRideRequest(dto, userId)
        );

        verify(rideRequestRepository, times(0)).save(any());
        verify(rideService, times(0)).save(any());
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
    }

    @Test
    @Order(9)
    @DisplayName("09 - Should throw RideScheduleTooLateException when scheduled more than 5h ahead")
    void shouldThrowWhenScheduleTooLate() {

        Long userId = 1L;

        RideRequestCreateDto dto = buildValidRequest();
        dto.schedule.type = "LATER";
        dto.schedule.startAt = Instant.now().plusSeconds(6 * 3600);

        User user = buildActiveUser(userId);
        VehicleType vehicleType = buildVehicleType(dto.vehicleTypeId);

        when(userService.getUser(userId))
                .thenReturn(Optional.of(user));

        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(vehicleType));

        when(rideEstimationService.estimate(
                eq(dto.route.points),
                eq(vehicleType)
        )).thenReturn(new RideEstimationResult(10.0, 12.0));

        // driver može da postoji — nije bitno
        when(driverService.findDriverForRideRequest(any(), any()))
                .thenReturn(buildAvailableDriver(10L));

        assertThrows(
                inc.visor.voom_service.exception.RideScheduleTooLateException.class,
                () -> rideRequestService.createRideRequest(dto, userId)
        );

        verifyNoInteractions(rideRequestRepository);
        verifyNoInteractions(rideService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(rideWsService);
        verifyNoInteractions(simulator);
    }

    @Test
    @Order(10)
    @DisplayName("10 - Should accept scheduled ride when exactly at 5h boundary")
    void shouldAcceptWhenExactly5HoursAhead() {

        Long userId = 1L;

        RideRequestCreateDto dto = buildValidRequest();
        dto.schedule.type = "LATER";
        dto.schedule.startAt = Instant.now().plusSeconds(5 * 3600 - 1);

        User user = buildActiveUser(userId);
        VehicleType vehicleType = buildVehicleType(dto.vehicleTypeId);
        Driver driver = buildAvailableDriver(10L);

        when(userService.getUser(userId))
                .thenReturn(Optional.of(user));

        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(vehicleType));

        when(rideEstimationService.estimate(
                eq(dto.route.points),
                eq(vehicleType)
        )).thenReturn(new RideEstimationResult(100.0, 200.0));

        when(driverService.findDriverForRideRequest(any(), any()))
                .thenReturn(driver);

        RideRequestResponseDto response
                = rideRequestService.createRideRequest(dto, userId);

        assertNotNull(response);
        assertEquals(RideRequestStatus.ACCEPTED, response.getStatus());
        assertNotNull(response.getDriver());

        verify(rideRequestRepository, times(1)).save(any());
        verify(rideService, times(1)).save(any());

        verify(notificationService, times(1))
                .createAndSendNotification(any(), any(), any(), any(), any());

        verifyNoInteractions(rideWsService);
        verifyNoInteractions(simulator);
    }

    @Test
    @Order(11)
    @DisplayName("11 - Should attach linked passengers to ride")
    void shouldAttachLinkedPassengers() {

        Long userId = 1L;

        RideRequestCreateDto dto = buildValidRequest();
        dto.schedule.type = "NOW";

        dto.linkedPassengers = List.of(
                "pera@mail.com",
                "mika@mail.com",
                "random@mail.com"
        );

        User creator = buildActiveUser(userId);

        Person creatorPerson = new Person();
        creatorPerson.setFirstName("Nikola");
        creatorPerson.setLastName("Bjelica");

        creator.setPerson(creatorPerson);

        VehicleType vehicleType = buildVehicleType(dto.vehicleTypeId);
        Driver driver = buildAvailableDriver(10L);

        User pera = new User();
        pera.setId(2L);

        User mika = new User();
        mika.setId(3L);

        when(userService.getUser(userId))
                .thenReturn(Optional.of(creator));

        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(vehicleType));

        when(rideEstimationService.estimate(
                eq(dto.route.points),
                eq(vehicleType)
        )).thenReturn(new RideEstimationResult(10.0, 20.0));

        when(driverService.findDriverForRideRequest(any(), any()))
                .thenReturn(driver);

        when(driverService.updateDriver(driver))
                .thenReturn(driver);

        when(userService.getUser("pera@mail.com"))
                .thenReturn(Optional.of(pera));

        when(userService.getUser("mika@mail.com"))
                .thenReturn(Optional.of(mika));

        when(userService.getUser("random@mail.com"))
                .thenReturn(Optional.empty());

        ArgumentCaptor<Ride> rideCaptor
                = ArgumentCaptor.forClass(Ride.class);

        RideRequestResponseDto response
                = rideRequestService.createRideRequest(dto, userId);

        assertNotNull(response);
        assertEquals(RideRequestStatus.ACCEPTED, response.getStatus());

        verify(rideService).save(rideCaptor.capture());
        Ride savedRide = rideCaptor.getValue();

        List<User> passengers = savedRide.getPassengers();

        assertEquals(2, passengers.size());
        assertTrue(passengers.contains(pera));
        assertTrue(passengers.contains(mika));
    }

    @Test
    @Order(12)
    @DisplayName("12 - Should send correct notification types for NOW ride")
    void shouldSendCorrectNotificationTypesForNowRide() {

        Long userId = 1L;

        User user = buildActiveUser(userId);
        VehicleType vehicleType = buildVehicleType(1L);
        Driver driver = buildAvailableDriver(10L);

        RideRequestCreateDto dto = buildValidRequest();

        when(userService.getUser(userId))
                .thenReturn(Optional.of(user));

        when(vehicleTypeService.getVehicleType(1L))
                .thenReturn(Optional.of(vehicleType));

        when(rideEstimationService.estimate(
                eq(dto.route.points),
                eq(vehicleType)
        )).thenReturn(new RideEstimationResult(10.0, 12.5));

        when(driverService.findDriverForRideRequest(
                any(),
                eq(dto.getFreeDriversSnapshot())
        )).thenReturn(driver);

        when(driverService.updateDriver(driver))
                .thenReturn(driver);

        rideRequestService.createRideRequest(dto, userId);

        ArgumentCaptor<User> recipientCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<NotificationType> typeCaptor
                = ArgumentCaptor.forClass(NotificationType.class);

        verify(notificationService, times(2))
                .createAndSendNotification(
                        recipientCaptor.capture(),
                        typeCaptor.capture(),
                        any(),
                        any(),
                        any()
                );

        List<inc.visor.voom_service.shared.notification.model.enums.NotificationType> sentTypes
                = typeCaptor.getAllValues();

        assertEquals(2, sentTypes.size());
        assertTrue(sentTypes.contains(
                inc.visor.voom_service.shared.notification.model.enums.NotificationType.RIDE_ASSIGNED));
        assertTrue(sentTypes.contains(
                inc.visor.voom_service.shared.notification.model.enums.NotificationType.RIDE_ACCEPTED));
    }

    @Test
    @Order(13)
    @DisplayName("13 - Should not update driver status for SCHEDULED ride")
    void shouldNotUpdateDriverForScheduledRide() {

        Long userId = 1L;

        RideRequestCreateDto dto = buildValidRequest();
        dto.schedule.type = "LATER";
        dto.schedule.startAt = Instant.now().plusSeconds(3600);

        User user = buildActiveUser(userId);
        VehicleType vehicleType = buildVehicleType(dto.vehicleTypeId);
        Driver driver = buildAvailableDriver(10L);

        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(vehicleType));
        when(rideEstimationService.estimate(any(), eq(vehicleType)))
                .thenReturn(new RideEstimationResult(10.0, 20.0));
        when(driverService.findDriverForRideRequest(any(), any()))
                .thenReturn(driver);

        rideRequestService.createRideRequest(dto, userId);

        verify(driverService, times(0)).updateDriver(any());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    @Order(14)
    @DisplayName("14 - Should not duplicate linked passengers")
    void shouldNotDuplicateLinkedPassengers() {

        Long userId = 1L;

        RideRequestCreateDto dto = buildValidRequest();
        dto.linkedPassengers = List.of("pera@mail.com", "pera@mail.com");

        User creator = buildActiveUser(userId);
        Person creatorPerson = new Person();
        creatorPerson.setFirstName("Nikola");
        creatorPerson.setLastName("Bjelica");

        creator.setPerson(creatorPerson);
        
        VehicleType vehicleType = buildVehicleType(dto.vehicleTypeId);
        Driver driver = buildAvailableDriver(10L);

        User pera = new User();
        pera.setId(2L);

        when(userService.getUser(userId)).thenReturn(Optional.of(creator));
        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(vehicleType));
        when(rideEstimationService.estimate(any(), eq(vehicleType)))
                .thenReturn(new RideEstimationResult(10.0, 20.0));
        when(driverService.findDriverForRideRequest(any(), any()))
                .thenReturn(driver);
        when(driverService.updateDriver(driver)).thenReturn(driver);
        when(userService.getUser("pera@mail.com"))
                .thenReturn(Optional.of(pera));

        ArgumentCaptor<Ride> rideCaptor
                = ArgumentCaptor.forClass(Ride.class);

        rideRequestService.createRideRequest(dto, userId);

        verify(rideService).save(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();

        assertEquals(1, savedRide.getPassengers().size());
    }

    @Test
    @Order(15)
    @DisplayName("15 - Should throw when route has less than 2 points")
    void shouldThrowWhenRouteInvalid() {

        Long userId = 1L;

        RideRequestCreateDto dto = buildValidRequest();
        dto.route.points = List.of(dto.route.points.get(0));

        when(userService.getUser(userId))
                .thenReturn(Optional.of(buildActiveUser(userId)));
        when(vehicleTypeService.getVehicleType(dto.vehicleTypeId))
                .thenReturn(Optional.of(buildVehicleType(dto.vehicleTypeId)));
        when(rideEstimationService.estimate(any(), any()))
                .thenReturn(new RideEstimationResult(10.0, 20.0));

        assertThrows(
                InvalidRouteOrderException.class,
                () -> rideRequestService.createRideRequest(dto, userId)
        );
    }

    private User buildActiveUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUserStatus(UserStatus.ACTIVE);
        return user;
    }

    private VehicleType buildVehicleType(Long id) {
        VehicleType vehicleType = new VehicleType();
        vehicleType.setId(id);
        return vehicleType;
    }

    private Driver buildAvailableDriver(Long id) {

        Person person = new Person();
        person.setFirstName("Nikola");
        person.setLastName("Bjelica");

        User driverUser = new User();
        driverUser.setPerson(person);

        Driver driver = new Driver();
        driver.setId(id);
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setUser(driverUser);

        return driver;
    }

    private static RideRequestCreateDto buildValidRequest() {

        RideRequestCreateDto dto = new RideRequestCreateDto();

        RideRequestCreateDto.RouteDto route = new RideRequestCreateDto.RouteDto();

        RideRequestCreateDto.RoutePointDto p1 = new RideRequestCreateDto.RoutePointDto();
        p1.lat = 45.0;
        p1.lng = 19.0;
        p1.orderIndex = 0;
        p1.type = "PICKUP";
        p1.address = "Start";

        RideRequestCreateDto.RoutePointDto p2 = new RideRequestCreateDto.RoutePointDto();
        p2.lat = 45.1;
        p2.lng = 19.1;
        p2.orderIndex = 1;
        p2.type = "STOP";
        p2.address = "End";

        route.points = List.of(p1, p2);
        dto.route = route;

        RideRequestCreateDto.ScheduleDto schedule = new RideRequestCreateDto.ScheduleDto();
        schedule.type = "NOW";
        schedule.startAt = Instant.now();
        dto.schedule = schedule;

        dto.vehicleTypeId = 1L;

        RideRequestCreateDto.PreferencesDto preferences = new RideRequestCreateDto.PreferencesDto();
        preferences.baby = false;
        preferences.pets = false;
        dto.preferences = preferences;

        dto.linkedPassengers = List.of();

        RideRequestCreateDto.DriverLocationDto driverLoc
                = new RideRequestCreateDto.DriverLocationDto();
        driverLoc.driverId = 10L;
        driverLoc.lat = 45.0;
        driverLoc.lng = 19.0;

        dto.freeDriversSnapshot = List.of(driverLoc);

        return dto;
    }

}
