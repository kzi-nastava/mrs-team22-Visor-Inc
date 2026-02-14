package inc.visor.voom_service.ride.finish;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.mail.EmailService;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.shared.notification.model.enums.NotificationType;
import inc.visor.voom_service.shared.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceFinishOngoingTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RideService rideService;
    
    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Ride exists")
        void testFindById_Good() {
            Long rideId = 1L;
            Ride mockRide = new Ride();
            mockRide.setId(rideId);

            when(rideRepository.findById(rideId)).thenReturn(Optional.of(mockRide));

            Ride result = rideService.findById(rideId);

            assertNotNull(result);
            assertEquals(rideId, result.getId());
        }

        @Test
        @DisplayName("Ride doesnt exist")
        void testFindById_Bad_NotFound() {
            Long rideId = 999L;
            when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> rideService.findById(rideId));
        }

        @Test
        @DisplayName("Null id")
        void testFindById_Edge_NullId() {
            when(rideRepository.findById(null)).thenThrow(new IllegalArgumentException("Id cannot be null"));

            assertThrows(IllegalArgumentException.class, () -> rideService.findById(null));
        }
    }


    @Nested
    @DisplayName("finishRide()")
    class FinishRideTests {

        @Test
        @DisplayName("Full flow")
        void testFinishRide_Good() {
            Long rideId = 1L;

            Ride ride = new Ride();
            ride.setId(rideId);

            User creator = new User();
            creator.setEmail("creator@gmail.com");
            creator.setId(10L);

            User passenger1 = new User();
            passenger1.setEmail("pass1@gmail.com");
            passenger1.setId(11L);

            RideRequest request = new RideRequest();
            request.setCreator(creator);
            request.setLinkedPassengerEmails(List.of("linked@gmail.com"));
            
            RideRoute route = new RideRoute();

            RoutePoint pickup = new RoutePoint();
            pickup.setPointType(RoutePointType.PICKUP);
            pickup.setAddress("Street 1, City, State, Country");

            RoutePoint dropoff = new RoutePoint();
            dropoff.setPointType(RoutePointType.DROPOFF);
            dropoff.setAddress("Street 2, City, State");

            route.setRoutePoints(List.of(pickup, dropoff));
            request.setRideRoute(route);

            ride.setRideRequest(request);
            ride.setPassengers(List.of(passenger1));

            when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

            rideService.finishRide(rideId);

            assertEquals(RideStatus.FINISHED, ride.getStatus());
            assertNotNull(ride.getFinishedAt());
            verify(rideRepository).save(ride);
            
            String expectedAddressString = "Street 1, City, State - Street 2, City, State";

            verify(emailService).sendRideCompletionEmail("creator@gmail.com", expectedAddressString);
            verify(emailService).sendRideCompletionEmail("linked@gmail.com", expectedAddressString);

            verify(notificationService).createAndSendNotification(
                    eq(creator),
                    eq(NotificationType.RIDE_FINISHED),
                    anyString(),
                    anyString(),
                    eq(rideId)
            );
            verify(notificationService).createAndSendNotification(
                    eq(passenger1),
                    eq(NotificationType.RIDE_FINISHED),
                    anyString(),
                    anyString(),
                    eq(rideId)
            );
        }

        @Test
        @DisplayName("Ride id not found")
        void testFinishRide_Bad_RideNotFound() {
            Long rideId = 999L;
            when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> rideService.finishRide(rideId));

            verifyNoInteractions(emailService);
            verifyNoInteractions(notificationService);
        }

        @Test
        @DisplayName("Addresses are null")
        void testFinishRide_Edge_NullAddresses() {
            Long rideId = 2L;
            Ride ride = new Ride();
            ride.setId(rideId);

            User creator = new User();
            creator.setEmail("c@gmail.com");

            RideRequest request = new RideRequest();
            request.setCreator(creator);
            request.setLinkedPassengerEmails(Collections.emptyList());

            RideRoute route = new RideRoute();
            RoutePoint pickup = new RoutePoint();
            pickup.setPointType(RoutePointType.PICKUP);
            pickup.setAddress(null);

            RoutePoint dropoff = new RoutePoint();
            dropoff.setPointType(RoutePointType.DROPOFF);
            dropoff.setAddress(null);

            route.setRoutePoints(List.of(pickup, dropoff));
            request.setRideRoute(route);
            ride.setRideRequest(request);
            ride.setPassengers(Collections.emptyList());

            when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

            rideService.finishRide(rideId);

            verify(emailService).sendRideCompletionEmail(eq("c@gmail.com"), eq("null - null"));
        }

        @Test
        @DisplayName("Address with exactly 3 commas")
        void testFinishRide_Edge_ExactCommas() {

            Long rideId = 3L;
            Ride ride = setupRide(rideId);

            ride.getRideRequest().getRideRoute().getPickupPoint().setAddress("A, B, C, D");
            ride.getRideRequest().getRideRoute().getDropoffPoint().setAddress("X");

            when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

            rideService.finishRide(rideId);

            String expected = "A, B, C - X";
            verify(emailService).sendRideCompletionEmail(anyString(), eq(expected));
        }

        @Test
        @DisplayName("Empty passenger list")
        void testFinishRide_BarelyValid_NoPassengers() {
            Long rideId = 4L;
            Ride ride = setupRide(rideId);

            ride.setPassengers(new ArrayList<>());
            ride.getRideRequest().setLinkedPassengerEmails(new ArrayList<>());

            when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

            rideService.finishRide(rideId);

            verify(emailService, times(1)).sendRideCompletionEmail(anyString(), anyString());
            verify(notificationService, times(1)).createAndSendNotification(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Ride Request null")
        void testFinishRide_BarelyInvalid_NullRequest() {
            Long rideId = 5L;
            Ride ride = new Ride();
            ride.setId(rideId);
            ride.setRideRequest(null);

            when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

            assertThrows(NullPointerException.class, () -> rideService.finishRide(rideId));
        }

        private Ride setupRide(Long id) {
            Ride ride = new Ride();
            ride.setId(id);

            User creator = new User();
            creator.setEmail("basic@gmail.com");

            RideRequest request = new RideRequest();
            request.setCreator(creator);
            request.setLinkedPassengerEmails(new ArrayList<>());

            RideRoute route = new RideRoute();
            RoutePoint p = new RoutePoint();
            p.setPointType(RoutePointType.PICKUP);
            p.setAddress("P");
            RoutePoint d = new RoutePoint();
            d.setPointType(RoutePointType.DROPOFF);
            d.setAddress("D");

            route.setRoutePoints(List.of(p, d));
            request.setRideRoute(route);
            ride.setRideRequest(request);
            ride.setPassengers(new ArrayList<>());

            return ride;
        }
    }

}