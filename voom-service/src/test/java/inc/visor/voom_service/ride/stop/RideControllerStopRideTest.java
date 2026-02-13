package inc.visor.voom_service.ride.stop;

import inc.visor.voom_service.auth.dto.LoginDto;
import inc.visor.voom_service.auth.dto.TokenDto;
import inc.visor.voom_service.ride.service.RideEstimateService;
import inc.visor.voom_service.ride.stop.data.DriverData;
import inc.visor.voom_service.ride.stop.data.RideData;
import inc.visor.voom_service.ride.stop.data.RideRequestData;
import inc.visor.voom_service.ride.stop.data.RideRouteData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc(addFilters = false, print = MockMvcPrint.LOG_DEBUG)
@DisplayName("Stop Ride REST Controller Integration Tests")
public class RideControllerStopRideTest {

    protected MockMvc mockMvc;
    private TestRestTemplate restTemplatePlain;
    private DriverData driverData;
    private RideEstimateService rideEstimateService;
    private RideRouteData rideRouteData;
    private RideRequestData rideRequestData;
    private RideData rideServiceData;

    public RideControllerStopRideTest(MockMvc mockMvc, TestRestTemplate restTemplatePlain, DriverData driverData, RideEstimateService rideEstimateService, RideRouteData rideRouteData, RideRequestData rideRequestData, RideData rideServiceData) {
        this.mockMvc = mockMvc;
        this.restTemplatePlain = restTemplatePlain;
        this.driverData = driverData;
        this.rideEstimateService = rideEstimateService;
        this.rideRouteData = rideRouteData;
        this.rideRequestData = rideRequestData;
        this.rideServiceData = rideServiceData;
    }

    @LocalServerPort
    private int port;

    private String userJwt;
    private TestRestTemplate restTemplate;
    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @BeforeEach
    void setUp() {
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

        RestTemplateBuilder builder = new RestTemplateBuilder(rt
                    -> rt.getInterceptors().add((request, body, execution) -> {
                request.getHeaders().add("Authorization", "Bearer " + userJwt);
                return execution.execute(request, body);
            })
        );

        this.restTemplate = new TestRestTemplate(builder);
    }

    @Test
    @DisplayName("Should successfully stop ride with valid data")
    void shouldSuccessfullyStopRide() throws Exception {

    }

    @Test
    @DisplayName("Should return 404 when driver not found")
    void shouldReturn404WhenDriverNotFound() throws Exception {

    }

    @Test
    @DisplayName("Should return 404 when ride not found")
    void shouldReturn404WhenRideNotFound() throws Exception {

    }

    @Test
    @DisplayName("Should return 404 when stop point not found in route")
    void shouldReturn404WhenStopPointNotInRoute() throws Exception {

    }

    @Test
    @DisplayName("Should handle stop at first route point")
    void shouldHandleStopAtFirstRoutePoint() throws Exception {

    }

    @Test
    @DisplayName("Should handle stop at last route point")
    void shouldHandleStopAtLastRoutePoint() throws Exception {

    }

    @Test
    @DisplayName("Should handle stop with coordinates requiring rounding")
    void shouldHandleStopWithCoordinateRounding() throws Exception {

    }

    @Test
    @DisplayName("Should update ride price based on actual distance")
    void shouldUpdateRidePriceBasedOnActualDistance() throws Exception {

    }
}
