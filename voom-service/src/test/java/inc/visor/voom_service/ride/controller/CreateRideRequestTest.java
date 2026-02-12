package inc.visor.voom_service.ride.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.complaints.service.ComplaintService;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.person.service.UserProfileService;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.service.FavoriteRouteService;
import inc.visor.voom_service.ride.service.RideEstimateService;
import inc.visor.voom_service.ride.service.RideRequestService;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.route.service.RideRouteService;
import inc.visor.voom_service.simulation.Simulator;
import inc.visor.voom_service.auth.token.service.JwtService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.exception.DriverNotAvailableException;

@WebMvcTest(RideController.class)
@AutoConfigureMockMvc
class RideControllerCreateRideRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideRequestService rideRequestService;

    @MockBean
    private FavoriteRouteService favoriteRouteService;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private ComplaintService complaintService;

    @MockBean
    private RideService rideService;

    @MockBean
    private Simulator simulatorService;

    @MockBean
    private DriverService driverService;

    @MockBean
    private UserService userService;

    @MockBean
    private RideRouteService rideRouteService;

    @MockBean
    private RideEstimateService rideEstimateService;

    @MockBean
    private RideWsService rideWsService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/rides/requests - should return 201 when user authenticated and valid request")
    void createRideRequest_success() throws Exception {
        VoomUserDetails principal = mock(VoomUserDetails.class);
        when(principal.getUsername()).thenReturn("user@test.com");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        when(userProfileService.getUserByEmail("user@test.com")).thenReturn(user);

        DriverSummaryDto driver = new DriverSummaryDto();
        driver.setFirstName("Nikola");
        driver.setLastName("Bjelica");

        RideRequestResponseDto responseDto
                = new RideRequestResponseDto(
                        100L,
                        RideRequestStatus.ACCEPTED,
                        12.5,
                        1500.0,
                        LocalDateTime.now(),
                        driver,
                        45.0,
                        19.0
                );

        when(rideRequestService.createRideRequest(any(RideRequestCreateDto.class), eq(1L))).thenReturn(responseDto);

        RideRequestCreateDto request = buildValidRequest();

        VoomUserDetails voomUser = mock(VoomUserDetails.class);
        when(voomUser.getUsername()).thenReturn("user@test.com");

        mockMvc.perform(post("/api/rides/requests")
                .with(user(voomUser))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestId").value(100L))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.price").value(1500.0))
                .andExpect(jsonPath("$.driver.firstName").value("Nikola"))
                .andExpect(jsonPath("$.pickupLat").value(45.0));
        
        verify(userProfileService).getUserByEmail(anyString());

        verify(rideRequestService, times(1)).createRideRequest(any(RideRequestCreateDto.class), eq(1L));
        verifyNoMoreInteractions(rideRequestService);
    }

    @Test
    @DisplayName("POST /api/rides/requests - should return 401 when user not authenticated")
    void createRideRequest_unauthorized() throws Exception {

        RideRequestCreateDto request = buildValidRequest();

        mockMvc.perform(post("/api/rides/requests")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(rideRequestService);
    }

    private static Stream<RideRequestCreateDto> invalidRequests() {

        RideRequestCreateDto base = buildValidRequest();

        // 1 route null
        RideRequestCreateDto r1 = buildValidRequest();
        r1.route = null;

        // 2 points null
        RideRequestCreateDto r2 = buildValidRequest();
        r2.route.points = null;

        // 3 points < 2
        RideRequestCreateDto r3 = buildValidRequest();
        r3.route.points = List.of(r3.route.points.get(0));

        // 4 schedule null
        RideRequestCreateDto r4 = buildValidRequest();
        r4.schedule = null;

        // 5 schedule.type blank
        RideRequestCreateDto r5 = buildValidRequest();
        r5.schedule.type = "";

        // 6 vehicleTypeId null
        RideRequestCreateDto r6 = buildValidRequest();
        r6.vehicleTypeId = null;

        // 7 preferences null
        RideRequestCreateDto r7 = buildValidRequest();
        r7.preferences = null;

        return Stream.of(r1, r2, r3, r4, r5, r6, r7);
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    @DisplayName("POST /api/rides/requests - should return 400 for invalid requests")
    void createRideRequest_invalidRequests(RideRequestCreateDto request) throws Exception {

        VoomUserDetails voomUser = mock(VoomUserDetails.class);
        when(voomUser.getUsername()).thenReturn("user@test.com");

        mockMvc.perform(post("/api/rides/requests")
                .with(user(voomUser))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(rideRequestService);
    }

    @Test
    @DisplayName("POST /api/rides/requests - should return 401 when user not found in database")
    void createRideRequest_userNotFound() throws Exception {

        RideRequestCreateDto request = buildValidRequest();

        VoomUserDetails voomUser = mock(VoomUserDetails.class);
        when(voomUser.getUsername()).thenReturn("user@test.com");

        when(userProfileService.getUserByEmail("user@test.com"))
                .thenReturn(null);

        mockMvc.perform(post("/api/rides/requests")
                .with(user(voomUser))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userProfileService).getUserByEmail("user@test.com");
        verifyNoInteractions(rideRequestService);
    }

    @Test
    @DisplayName("POST /api/rides/requests - should return 409 when no drivers available")
    void createRideRequest_noDrivers() throws Exception {

        RideRequestCreateDto request = buildValidRequest();

        VoomUserDetails voomUser = mock(VoomUserDetails.class);
        when(voomUser.getUsername()).thenReturn("user@test.com");

        User user = new User();
        user.setId(1L);

        when(userProfileService.getUserByEmail("user@test.com"))
                .thenReturn(user);

        when(rideRequestService.createRideRequest(any(), eq(1L)))
                .thenThrow(new DriverNotAvailableException());

        mockMvc.perform(post("/api/rides/requests")
                .with(user(voomUser))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    private static RideRequestCreateDto buildValidRequest() {
        RideRequestCreateDto dto = new RideRequestCreateDto();

        RideRequestCreateDto.RouteDto route = new RideRequestCreateDto.RouteDto();

        RideRequestCreateDto.RoutePointDto p1 = new RideRequestCreateDto.RoutePointDto();
        p1.lat = 45.0;
        p1.lng = 19.0;
        p1.orderIndex = 0;
        p1.type = "START";
        p1.address = "Start address";

        RideRequestCreateDto.RoutePointDto p2 = new RideRequestCreateDto.RoutePointDto();
        p2.lat = 45.1;
        p2.lng = 19.1;
        p2.orderIndex = 1;
        p2.type = "END";
        p2.address = "End address";

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

        dto.linkedPassengers = List.of("friend@test.com");

        return dto;
    }
}
