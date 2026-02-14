package inc.visor.voom_service.ride.finish;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.ride.dto.ActiveRideDto;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.service.RideService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceFinishOngoingTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RideService rideService;

    @InjectMocks
    private DriverService driverService;


    @Nested
    @DisplayName("getDriverFromUser()")
    class GetDriverFromUserTests {

        @Test
        @DisplayName("User id exists and Driver is found")
        void testGetDriverFromUser_Good() {
            long userId = 100L;
            Driver mockDriver = new Driver();
            mockDriver.setId(1L);

            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(mockDriver));

            Optional<Driver> result = driverService.getDriverFromUser(userId);

            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());
            verify(driverRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("User id does not exist")
        void testGetDriverFromUser_Bad() {
            long userId = 999L;
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());

            Optional<Driver> result = driverService.getDriverFromUser(userId);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("User id is zero")
        void testGetDriverFromUser_Edge_ZeroId() {
            long userId = 0L;
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());

            Optional<Driver> result = driverService.getDriverFromUser(userId);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("user id is negative")
        void testGetDriverFromUser_BarelyValid_NegativeId() {
            long userId = -1L;
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());

            Optional<Driver> result = driverService.getDriverFromUser(userId);
            assertTrue(result.isEmpty());
        }
    }


    @Nested
    @DisplayName("save()")
    class SaveTests {

        @Test
        @DisplayName("Saving a valid driver")
        void testSave_Good() {
            Driver driver = new Driver();
            driver.setUser(new User());

            when(driverRepository.save(driver)).thenReturn(driver);

            Driver result = driverService.save(driver);

            assertNotNull(result);
            verify(driverRepository).save(driver);
        }

        @Test
        @DisplayName("Passing null driver")
        void testSave_Bad_NullInput() {
            doThrow(new IllegalArgumentException("Entity must not be null")).when(driverRepository).save(null);

            assertThrows(IllegalArgumentException.class, () -> {
                driverService.save(null);
            });
        }

        @Test
        @DisplayName("Saving empty driver object")
        void testSave_BarelyValid_EmptyObject() {
            Driver driver = new Driver();

            when(driverRepository.save(driver)).thenReturn(driver);

            Driver result = driverService.save(driver);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Saving a driver updated")
        void testSave_Edge_ExistingId() {
            Driver driver = new Driver();
            driver.setId(500L);
            driver.setStatus(DriverStatus.BUSY);

            when(driverRepository.save(driver)).thenReturn(driver);

            Driver result = driverService.save(driver);
            assertEquals(500L, result.getId());
            verify(driverRepository).save(driver);
            assertEquals(DriverStatus.BUSY, result.getStatus());
        }
    }

    @Nested
    @DisplayName("getActiveRide()")
    class GetActiveRideTests {

        @Test
        @DisplayName("Active ride exists full object")
        void testGetActiveRide_Good() {
            Long userId = 1L;

            Ride ride = new Ride();
            ride.setId(10L);
            ride.setStatus(RideStatus.ONGOING);

            RideRequest request = new RideRequest();
            RideRoute route = new RideRoute();
            RoutePoint point = new RoutePoint();
            point.setLatitude(44.0);
            point.setLongitude(21.0);
            point.setPointType(RoutePointType.PICKUP);

            route.setRoutePoints(List.of(point));
            request.setRideRoute(route);
            ride.setRideRequest(request);

            when(rideService.findActiveRide(userId)).thenReturn(ride);

            ActiveRideDto result = driverService.getActiveRide(userId);

            assertNotNull(result);
            assertEquals(10L, result.getRideId());
            assertEquals(RideStatus.ONGOING, result.getStatus());
            assertEquals(1, result.getRoutePoints().size());
            assertEquals(44.0, result.getRoutePoints().getFirst().getLat());
        }

        @Test
        @DisplayName("No active ride found")
        void testGetActiveRide_Bad_NotFound() {
            Long userId = 1L;
            when(rideService.findActiveRide(userId)).thenReturn(null);

            ActiveRideDto result = driverService.getActiveRide(userId);

            assertNull(result);
            verify(rideService).findActiveRide(userId);
        }

        @Test
        @DisplayName("Ride exists but RoutePoints list is empty")
        void testGetActiveRide_Edge_EmptyRoutePoints() {
            Long userId = 1L;

            Ride ride = new Ride();
            ride.setId(11L);

            RideRequest request = new RideRequest();
            RideRoute route = new RideRoute();
            route.setRoutePoints(Collections.emptyList());

            request.setRideRoute(route);
            ride.setRideRequest(request);

            when(rideService.findActiveRide(userId)).thenReturn(ride);

            ActiveRideDto result = driverService.getActiveRide(userId);

            assertNotNull(result);
            assertTrue(result.getRoutePoints().isEmpty());
        }

        @Test
        @DisplayName("Ride exists but RideRequest is null")
        void testGetActiveRide_BarelyInvalid_NullRequest() {
            Long userId = 1L;
            Ride ride = new Ride();
            ride.setId(12L);
            ride.setRideRequest(null);

            when(rideService.findActiveRide(userId)).thenReturn(ride);

            assertThrows(NullPointerException.class, () -> {
                driverService.getActiveRide(userId);
            });
        }

        @Test
        @DisplayName("RideRequest exists but RideRoute is null")
        void testGetActiveRide_BarelyInvalid_NullRoute() {
            Long userId = 1L;
            Ride ride = new Ride();
            RideRequest request = new RideRequest();
            request.setRideRoute(null);
            ride.setRideRequest(request);

            when(rideService.findActiveRide(userId)).thenReturn(ride);

            assertThrows(NullPointerException.class, () -> {
                driverService.getActiveRide(userId);
            });
        }

        @Test
        @DisplayName("RideRoute exists but RoutePoints list is null")
        void testGetActiveRide_BarelyInvalid_NullPointsList() {
            Long userId = 1L;
            Ride ride = new Ride();
            RideRequest request = new RideRequest();
            RideRoute route = new RideRoute();
            route.setRoutePoints(null);

            request.setRideRoute(route);
            ride.setRideRequest(request);

            when(rideService.findActiveRide(userId)).thenReturn(ride);

            assertThrows(NullPointerException.class, () -> {
                driverService.getActiveRide(userId);
            });
        }
    }

}