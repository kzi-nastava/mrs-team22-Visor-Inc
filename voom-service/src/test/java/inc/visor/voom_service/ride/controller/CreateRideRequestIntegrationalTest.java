package inc.visor.voom_service.ride.controller;

import java.time.Instant;
import java.util.List;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import inc.visor.voom_service.auth.dto.LoginDto;
import inc.visor.voom_service.auth.dto.TokenDto;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateRideRequestIntegrationalTest {

    @Autowired
    private TestRestTemplate restTemplatePlain;

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
    @Order(2)
    @DisplayName("02 - Should create ride request and return REJECTED status when no driver accepts")
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
        assertEquals(RideRequestStatus.REJECTED, body.getStatus());
        assertTrue(body.getPrice() >= 0);
        assertTrue(body.getDistanceKm() > 0);
        assertEquals(45.0, body.getPickupLat());
        assertEquals(19.0, body.getPickupLng());

    }

    private RideRequestCreateDto buildValidRequest() {

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
