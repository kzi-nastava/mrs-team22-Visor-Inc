package inc.visor.voom_service.ride.stop;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.ride.controller.RideController;
import inc.visor.voom_service.ride.service.RideEstimateService;
import inc.visor.voom_service.ride.service.RideRequestService;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.route.service.RideRouteService;
import inc.visor.voom_service.simulation.Simulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc(addFilters = false, print = MockMvcPrint.LOG_DEBUG)
@DisplayName("Stop Ride REST Controller Integration Tests")
public class RideControllerStopRideTest {

    @Autowired
    private final MockMvc mockMvc;

    @Autowired
    private final ObjectMapper objectMapper;

    @Autowired
    private final DriverService driverService;

    @Autowired
    private final RideService rideService;

    @Autowired
    private final RideRequestService rideRequestService;

    @Autowired
    private final RideRouteService rideRouteService;

    @Autowired
    private final RideEstimateService rideEstimateService;

    @Autowired
    private final RideWsService rideWsService;

    @Autowired
    private final Simulator simulator;

    public RideControllerStopRideTest(MockMvc mockMvc, ObjectMapper objectMapper, DriverService driverService, RideService rideService, RideRequestService rideRequestService, RideRouteService rideRouteService, RideEstimateService rideEstimateService, RideWsService rideWsService, Simulator simulator) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.driverService = driverService;
        this.rideService = rideService;
        this.rideRequestService = rideRequestService;
        this.rideRouteService = rideRouteService;
        this.rideEstimateService = rideEstimateService;
        this.rideWsService = rideWsService;
        this.simulator = simulator;
    }

    @BeforeEach
    void setUp() {

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
