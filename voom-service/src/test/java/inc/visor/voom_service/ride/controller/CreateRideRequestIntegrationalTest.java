package inc.visor.voom_service.ride.controller;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import inc.visor.voom_service.auth.dto.LoginDto;
import inc.visor.voom_service.auth.dto.TokenDto;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.repository.RideRequestRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CreateRideRequestIntegrationalTest {

    @Autowired
    private TestRestTemplate restTemplatePlain;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplateUser;
    private String userJwt;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @BeforeEach
    void setup() {
        loginAndPrepareRestTemplate();
    }

    private void loginAndPrepareRestTemplate() {

        LoginDto login = new LoginDto();
        login.setEmail("user@test.com");
        login.setPassword("test1234");

        ResponseEntity<TokenDto> loginResponse
                = restTemplatePlain.postForEntity(
                        getBaseUrl() + "/auth/login",
                        login,
                        TokenDto.class
                );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());

        this.userJwt = loginResponse.getBody().getAccessToken();

        RestTemplateBuilder builder
                = new RestTemplateBuilder(rt
                        -> rt.getInterceptors().add((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", "Bearer " + userJwt);
                    return execution.execute(request, body);
                })
                );

        this.restTemplateUser = new TestRestTemplate(builder);
    }

    @Test
    @Order(1)
    @DisplayName("01 - Should create ride request and return ACCEPTED status when a driver accepts")
    void shouldCreateRideRequest() {

        RideRequestCreateDto request = buildValidRequest();

        ResponseEntity<RideRequestResponseDto> response
                = restTemplateUser.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        RideRequestResponseDto.class
                );

        RideRequestResponseDto body = response.getBody();

        assertNotNull(body);
        assertTrue(body.getRequestId() > 0);
        assertEquals(RideRequestStatus.ACCEPTED, body.getStatus());
        assertTrue(body.getPrice() >= 0);
        assertTrue(body.getDistanceKm() > 0);
        assertEquals(45.0, body.getPickupLat());
        assertEquals(19.0, body.getPickupLng());

        assertTrue(
                rideRequestRepository.findById(body.getRequestId()).isPresent(),
                "RideRequest should be persisted in database"
        );

    }

    @Test
    @Order(2)
    @DisplayName("02 - Should return UNAUTHORIZED when creating ride request without JWT")
    void shouldReturnUnauthorizedWhenJwtMissing() {

        RideRequestCreateDto request = buildValidRequest();

        ResponseEntity<String> response
                = restTemplatePlain.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        String.class
                );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }

    @Test
    @Order(3)
    @DisplayName("03 - Should return REJECTED when no driver matches requested vehicle type")
    void shouldReturnRejectedWhenNoDriverMatchesVehicleType() {

        RideRequestCreateDto request = buildValidRequest();
        request.vehicleTypeId = 3L;

        ResponseEntity<RideRequestResponseDto> response
                = restTemplateUser.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        RideRequestResponseDto.class
                );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        RideRequestResponseDto body = response.getBody();

        assertNotNull(body);
        assertTrue(body.getRequestId() > 0);
        assertEquals(RideRequestStatus.REJECTED, body.getStatus());

        assertTrue(
                rideRequestRepository.findById(body.getRequestId()).isPresent(),
                "RideRequest should be persisted in database"
        );

    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    @DisplayName("04 - Should return BAD_REQUEST for invalid ride requests")
    void shouldReturnBadRequestForInvalidInputs(RideRequestCreateDto request) {

        ResponseEntity<String> response
                = restTemplateUser.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        String.class
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private static Stream<RideRequestCreateDto> invalidRequests() {

        RideRequestCreateDto r1 = buildValidRequest();
        r1.route = null;

        RideRequestCreateDto r2 = buildValidRequest();
        r2.route.points = null;

        RideRequestCreateDto r3 = buildValidRequest();
        r3.route.points = List.of(r3.route.points.get(0));

        RideRequestCreateDto r4 = buildValidRequest();
        r4.schedule = null;

        RideRequestCreateDto r5 = buildValidRequest();
        r5.vehicleTypeId = null;

        RideRequestCreateDto r6 = buildValidRequest();
        r6.preferences = null;

        return Stream.of(r1, r2, r3, r4, r5, r6);
    }

    @Test
    @Order(4)
    @DisplayName("04 - Should forbid ride request when user is suspended")
    void shouldReturnForbiddenWhenUserSuspended() {

        User user = userRepository.findByEmail("user@test.com").orElseThrow();

        user.setUserStatus(UserStatus.SUSPENDED);
        userRepository.save(user);

        RideRequestCreateDto request = buildValidRequest();

        ResponseEntity<String> response
                = restTemplateUser.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        String.class
                );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Order(5)
    @DisplayName("05 - Should return REJECTED when no driver is available")
    @Sql(scripts = "/sql/driver-busy.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldReturnRejectedWhenNoDriverIsAvailable() {

        RideRequestCreateDto request = buildValidRequest();

        ResponseEntity<RideRequestResponseDto> response
                = restTemplateUser.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        RideRequestResponseDto.class
                );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        RideRequestResponseDto body = response.getBody();

        assertNotNull(body);
        assertTrue(body.getRequestId() > 0);
        assertEquals(RideRequestStatus.REJECTED, body.getStatus());

        assertTrue(
                rideRequestRepository.findById(body.getRequestId()).isPresent(),
                "RideRequest should be persisted in database"
        );
    }

    @Test
    @Order(6)
    @DisplayName("06 - Should create scheduled ride and return ACCEPTED")
    void shouldCreateScheduledRide() {

        RideRequestCreateDto request = buildValidRequest();

        request.schedule.type = "LATER";
        request.schedule.startAt = Instant.now().plusSeconds(3600);

        ResponseEntity<RideRequestResponseDto> response
                = restTemplateUser.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        RideRequestResponseDto.class
                );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        RideRequestResponseDto body = response.getBody();
        assertNotNull(body);

        assertEquals(RideRequestStatus.ACCEPTED, body.getStatus());
        assertNotNull(body.getDriver());

        assertTrue(
                rideRequestRepository.findById(body.getRequestId()).isPresent(),
                "RideRequest should be persisted"
        );
    }

    @Test
    @Order(7)
    @DisplayName("07 - Should reject scheduled ride and return REJECTED when no driver is available")
    @Sql(scripts = "/sql/driver-busy.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRejectScheduledRideWhenNoDriverAvailable() {

        RideRequestCreateDto request = buildValidRequest();

        request.schedule.type = "LATER";
        request.schedule.startAt = Instant.now().plusSeconds(3600);

        ResponseEntity<RideRequestResponseDto> response
                = restTemplateUser.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        RideRequestResponseDto.class
                );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        RideRequestResponseDto body = response.getBody();
        assertNotNull(body);

        assertEquals(RideRequestStatus.REJECTED, body.getStatus());
        assertNotNull(body.getDriver());

        assertTrue(
                rideRequestRepository.findById(body.getRequestId()).isPresent(),
                "RideRequest should be persisted"
        );
    }

    @Test
    @Order(8)
    @DisplayName("08 - Should return BAD_REQUEST when scheduled more than 5 hours ahead")
    void shouldRejectWhenScheduledTooLate() {

        RideRequestCreateDto request = buildValidRequest();

        request.schedule.type = "LATER";
        request.schedule.startAt = Instant.now().plusSeconds(6 * 3600); 

        ResponseEntity<String> response
                = restTemplateUser.postForEntity(
                        getBaseUrl() + "/rides/requests",
                        request,
                        String.class
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        driverLoc.driverId = 1L;
        driverLoc.lat = 45.0;
        driverLoc.lng = 19.0;

        dto.freeDriversSnapshot = List.of(driverLoc);

        return dto;
    }

}
