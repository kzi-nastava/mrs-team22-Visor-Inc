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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;



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

    @MockBean(name = "simulatorService")
    private Simulator simulatorService;

    @MockBean(name = "simulator")
    private Simulator simulator;

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

        RideRequestResponseDto responseDto = mock(RideRequestResponseDto.class);
        when(rideRequestService.createRideRequest(any(RideRequestCreateDto.class), eq(1L))).thenReturn(responseDto);

        RideRequestCreateDto request = buildValidRequest();

        VoomUserDetails voomUser = mock(VoomUserDetails.class);
        when(voomUser.getUsername()).thenReturn("user@test.com");

        mockMvc.perform(post("/api/rides/requests")
        .with(user(voomUser))
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());


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
