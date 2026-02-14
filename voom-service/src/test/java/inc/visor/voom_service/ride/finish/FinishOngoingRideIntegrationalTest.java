package inc.visor.voom_service.ride.finish;

import inc.visor.voom_service.auth.dto.LoginDto;
import inc.visor.voom_service.auth.dto.TokenDto;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.mail.EmailService;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.ride.dto.ActiveRideDto;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.repository.RideRequestRepository;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.shared.notification.service.NotificationService;
import inc.visor.voom_service.simulation.Simulator;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Finish Ongoing Ride Integration Tests")
public class FinishOngoingRideIntegrationalTest {

    @Autowired
    private TestRestTemplate restTemplatePlain;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private RideService rideService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private RideWsService rideWsService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private Simulator simulator;

    @LocalServerPort
    private int port;

    private String driverJwt;
    private TestRestTemplate restTemplate;

    private Driver driver;
    private User driverUser;
    private Ride ride;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @BeforeEach
    void setUp() {
        initializeData();
        loginAndPrepareRestTemplate();
    }

    private void loginAndPrepareRestTemplate() {
        final LoginDto login = new LoginDto();
        login.setEmail(driverUser.getEmail());
        login.setPassword("test1234");

        ResponseEntity<TokenDto> loginResponse = restTemplatePlain.postForEntity(getBaseUrl() + "/auth/login", login, TokenDto.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Driver login failed");
        assertNotNull(loginResponse.getBody());

        this.driverJwt = loginResponse.getBody().getAccessToken();

        final RestTemplateBuilder builder = new RestTemplateBuilder(template -> template.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + driverJwt);
            return execution.execute(request, body);
        }));

        this.restTemplate = new TestRestTemplate(builder);
    }

    private void initializeData() {

        driver = driverRepository.findById(1L).orElseThrow(() -> new RuntimeException("Driver ID 1 not found"));
        driverUser = driver.getUser();

        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        VehicleType vehicleType = vehicleTypeRepository.findAll().stream().findFirst().orElseThrow();
        User creator = userRepository.findAll().stream().filter(u -> u.getId() != driverUser.getId()).findFirst().orElseThrow();

        RoutePoint pickup = new RoutePoint();
        pickup.setLatitude(45.235);
        pickup.setLongitude(19.822);
        pickup.setAddress("Bulevar Oslobodjenja 1, Novi Sad");
        pickup.setPointType(RoutePointType.PICKUP);
        pickup.setOrderIndex(0);

        RoutePoint dropoff = new RoutePoint();
        dropoff.setLatitude(45.240);
        dropoff.setLongitude(19.830);
        dropoff.setAddress("Zeleznicka Stanica, Novi Sad");
        dropoff.setPointType(RoutePointType.DROPOFF);
        dropoff.setOrderIndex(1);

        RideRoute route = new RideRoute();
        route.setTotalDistanceKm(2.5);
        route.setRoutePoints(List.of(pickup, dropoff));

        RideRequest request = new RideRequest();
        request.setCreator(creator);
        request.setRideRoute(route);
        request.setStatus(RideRequestStatus.ACCEPTED);
        request.setScheduleType(ScheduleType.NOW);
        request.setVehicleType(vehicleType);
        request.setBabyTransport(false);
        request.setPetTransport(false);
        request.setCalculatedPrice(500.0);
        request.setLinkedPassengerEmails(new ArrayList<>());
        request = rideRequestRepository.save(request);

        ride = new Ride();
        ride.setRideRequest(request);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ONGOING);
        ride.setStartedAt(LocalDateTime.now().minusMinutes(10));
        ride.setPassengers(List.of());
        ride = rideService.save(ride);

    }

    @Test
    @DisplayName("Should successfully finish ongoing ride")
    void shouldSuccessfullyFinishRide() {

        ResponseEntity<ActiveRideDto> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/finish-ongoing",
                null,
                ActiveRideDto.class
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(ride.getId(), response.getBody().getRideId());

        Ride updatedRide = rideRepository.findById(ride.getId()).orElseThrow();
        assertEquals(RideStatus.FINISHED, updatedRide.getStatus(), "Ride status should be FINISHED in DB");
        assertNotNull(updatedRide.getFinishedAt(), "FinishedAt should be set");

        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        assertEquals(DriverStatus.AVAILABLE, updatedDriver.getStatus(), "Driver should be AVAILABLE after finish");
    }

    @Test
    @DisplayName("Should return not found if driver has no active ride")
    void shouldFailWhenNoActiveRide() {
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/finish-ongoing",
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return unauthorized if driver isnt authenticated")
    void shouldFailWhenNotAuthenticated() {
        ResponseEntity<String> response = restTemplatePlain.postForEntity(
                getBaseUrl() + "/rides/finish-ongoing",
                null,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should trigger emails and notifications")
    void shouldTriggerSideEffects() {

        restTemplate.postForEntity(getBaseUrl() + "/rides/finish-ongoing", null, ActiveRideDto.class);


        verify(emailService, times(1)).sendRideCompletionEmail(
                eq(ride.getRideRequest().getCreator().getEmail()),
                anyString()
        );

        verify(notificationService, atLeastOnce()).createAndSendNotification(
                any(User.class),
                eq(inc.visor.voom_service.shared.notification.model.enums.NotificationType.RIDE_FINISHED),
                anyString(),
                anyString(),
                eq(ride.getId())
        );

        verify(rideWsService, times(1)).sendRideChanges(any());

        verify(simulator, times(1)).setFinishedRide(eq(driverUser.getId()));
    }

    @Test
    @DisplayName("Should return error if authenticated user is not a driver")
    void shouldFailWhenUserIsNotDriver() {

        User existingUser = userRepository.findAll().stream()
                .filter(u -> !u.getEmail().equals(driverUser.getEmail()))
                .findFirst()
                .orElseThrow();

        LoginDto login = new LoginDto();
        login.setEmail(existingUser.getEmail());
        login.setPassword("test1234");

        ResponseEntity<TokenDto> loginResponse = restTemplatePlain.postForEntity(
                getBaseUrl() + "/auth/login", login, TokenDto.class
        );
        String userJwt = loginResponse.getBody().getAccessToken();

        RestTemplateBuilder userBuilder = new RestTemplateBuilder(template -> template.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + userJwt);
            return execution.execute(request, body);
        }));
        TestRestTemplate userRestTemplate = new TestRestTemplate(userBuilder);

        ResponseEntity<String> response = userRestTemplate.postForEntity(
                getBaseUrl() + "/rides/finish-ongoing",
                null,
                String.class
        );


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return not found if ride didnt start yet")
    void shouldFailWhenRideNotStarted() {
        ride.setStatus(RideStatus.SCHEDULED);
        rideRepository.save(ride);

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/finish-ongoing",
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}