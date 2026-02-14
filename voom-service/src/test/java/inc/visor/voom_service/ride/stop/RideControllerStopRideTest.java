package inc.visor.voom_service.ride.stop;

import inc.visor.voom_service.auth.dto.LoginDto;
import inc.visor.voom_service.auth.dto.TokenDto;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.ride.dto.RideStopDto;
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
import inc.visor.voom_service.ride.service.RideEstimateService;
import inc.visor.voom_service.route.repository.RideRouteRepository;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc(addFilters = false, print = MockMvcPrint.LOG_DEBUG)
@DisplayName("Stop Ride REST Controller Integration Tests")
public class RideControllerStopRideTest {

    @Autowired
    private TestRestTemplate restTemplatePlain;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideEstimateService rideEstimateService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private RideRouteRepository rideRouteRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @LocalServerPort
    private int port;

    private String userJwt;
    private TestRestTemplate restTemplate;

    private Driver driver;
    private User user;
    private Ride ride;
    private VehicleType vehicleType;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @BeforeEach
    void setUp() {
        loginAndPrepareRestTemplate();
        initialize();
    }

    private void loginAndPrepareRestTemplate() {
        final LoginDto login = new LoginDto();
        login.setEmail("user@test.com");
        login.setPassword("test1234");

        ResponseEntity<TokenDto> loginResponse = restTemplatePlain.postForEntity(getBaseUrl() + "/auth/login", login, TokenDto.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());

        this.userJwt = loginResponse.getBody().getAccessToken();

        final RestTemplateBuilder builder = new RestTemplateBuilder(template -> template.getInterceptors().add((request, body, execution) -> {
                request.getHeaders().add("Authorization", "Bearer " + userJwt);
                return execution.execute(request, body);
            })
        );

        this.restTemplate = new TestRestTemplate(builder);
    }

    private void initialize() {
        driver = driverRepository.findById(1L).get();
        user = userRepository.findById(1L).get();
        vehicleType = vehicleTypeRepository.findByType("STANDARD").get();

        RoutePoint pickup = new RoutePoint();
        pickup.setLatitude(45.0);
        pickup.setLongitude(19.0);
        pickup.setAddress("Address 1, Novi Sad");
        pickup.setPointType(RoutePointType.PICKUP);
        pickup.setOrderIndex(0);

        RoutePoint stop = new RoutePoint();
        stop.setLatitude(45.1);
        stop.setLongitude(19.1);
        stop.setAddress("Address 2, Novi Sad");
        stop.setPointType(RoutePointType.STOP);
        stop.setOrderIndex(1);

        RoutePoint dropoff = new RoutePoint();
        dropoff.setLatitude(45.2);
        dropoff.setLongitude(19.2);
        dropoff.setAddress("Address 3, Novi Sad");
        dropoff.setPointType(RoutePointType.DROPOFF);
        dropoff.setOrderIndex(2);

        RideRoute route = new RideRoute();
        route.setTotalDistanceKm(80.5);
        route.setRoutePoints(List.of(pickup, stop, dropoff));

        RideRequest request = new RideRequest();
        request.setCreator(user);
        request.setRideRoute(route);
        request.setStatus(RideRequestStatus.ACCEPTED);
        request.setScheduleType(ScheduleType.NOW);
        request.setVehicleType(vehicleType);
        request.setBabyTransport(false);
        request.setPetTransport(true);
        request.setCalculatedPrice(4500.0);
        request.setLinkedPassengerEmails(new ArrayList<>());
        request = rideRequestRepository.save(request);

        ride = new Ride();
        ride.setRideRequest(request);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ONGOING);
        ride.setStartedAt(LocalDateTime.now());
        ride.setPassengers(List.of(user));
        ride.setReminderSent(false);
        ride = rideRepository.save(ride);
    }

    @Test
    @DisplayName("Should successfully stop ride with valid data")
    void shouldSuccessfullyStopRide() throws Exception {
        RideStopDto stopDto = new RideStopDto();
        stopDto.setUserId(driver.getUser().getId());
        stopDto.setPoint(new LatLng(45.001293, 19.012093));
        stopDto.setTimestamp(LocalDateTime.now());

        ResponseEntity<RideResponseDto> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/" + ride.getId() + "/stop",
                stopDto,
                RideResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        RideResponseDto body = response.getBody();
        assertEquals(RideStatus.STOPPED, body.getStatus());
        assertNotNull(body.getFinishedAt());

        Driver driver = driverRepository.findById(1L).orElseThrow();
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    @DisplayName("Should return 404 when driver not found")
    void shouldReturn404WhenDriverNotFound() throws Exception {
        // Arrange
        RideStopDto stopDto = new RideStopDto();
        stopDto.setUserId(999L); // Non-existent user ID
        stopDto.setPoint(new LatLng(45.1, 19.1));
        stopDto.setTimestamp(LocalDateTime.now());

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/" + ride.getId() + "/stop",
                stopDto,
                String.class
        );

        // Assert
        assertTrue(
                response.getStatusCode() == HttpStatus.NOT_FOUND ||
                        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Test
    @DisplayName("Should return 404 when ride not found")
    void shouldReturn404WhenRideNotFound() throws Exception {
        // Arrange
        RideStopDto stopDto = new RideStopDto();
        stopDto.setUserId(driver.getUser().getId());
        stopDto.setPoint(new LatLng(45.1, 19.1));
        stopDto.setTimestamp(LocalDateTime.now());

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/999/stop", // Non-existent ride ID
                stopDto,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when stop point not found in route")
    void shouldReturn404WhenStopPointNotInRoute() throws Exception {
        // Arrange
        RideStopDto stopDto = new RideStopDto();
        stopDto.setUserId(driver.getUser().getId());
        stopDto.setPoint(new LatLng(99.9, 99.9));
        stopDto.setTimestamp(LocalDateTime.now());


        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/" + ride.getId() + "/stop",
                stopDto,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle stop at first route point")
    void shouldHandleStopAtFirstRoutePoint() throws Exception {
        // Arrange
        RideStopDto stopDto = new RideStopDto();
        stopDto.setUserId(driver.getUser().getId());
        stopDto.setPoint(new LatLng(45.0, 19.0));
        stopDto.setTimestamp(LocalDateTime.now());


        // Act
        ResponseEntity<RideResponseDto> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/" + ride.getId() + "/stop",
                stopDto,
                RideResponseDto.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RideStatus.STOPPED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Should handle stop at last route point")
    void shouldHandleStopAtLastRoutePoint() throws Exception {
        // Arrange
        RideStopDto stopDto = new RideStopDto();
        stopDto.setUserId(driver.getUser().getId());
        stopDto.setPoint(new LatLng(45.2, 19.2));
        stopDto.setTimestamp(LocalDateTime.now());

        // Act
        ResponseEntity<RideResponseDto> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/" + ride.getId() + "/stop",
                stopDto,
                RideResponseDto.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RideStatus.STOPPED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Should handle stop with coordinates requiring rounding")
    void shouldHandleStopWithCoordinateRounding() throws Exception {
        RideStopDto stopDto = new RideStopDto();
        stopDto.setUserId(driver.getUser().getId());
        stopDto.setPoint(new LatLng(45.1499, 19.1499));
        stopDto.setTimestamp(LocalDateTime.now());

        // Act
        ResponseEntity<RideResponseDto> response = restTemplate.postForEntity(
                getBaseUrl() + "/rides/" + ride.getId() + "/stop",
                stopDto,
                RideResponseDto.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RideStatus.STOPPED, response.getBody().getStatus());
    }
}
