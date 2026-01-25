package inc.visor.voom_service.ride.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import inc.visor.voom_service.ride.model.enums.Sorting;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.ride.dto.ActiveRideDto;
import inc.visor.voom_service.ride.dto.RideLocationDto;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.route.service.RideRouteService;
import inc.visor.voom_service.shared.RoutePointDto;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RideRouteService routeService;

    public RideService(RideRepository rideRepository, RideRouteService routeService) {
        this.rideRepository = rideRepository;
        this.routeService = routeService;
    }

    public void updateRidePosition(RideLocationDto dto) {
        // get and set position
        return;
    }

    public void finishRide(Ride ride) {
        // set status
        return;
    }

    public List<Ride> getDriverRides(Long driverId, LocalDateTime start, LocalDateTime end, Sorting sort) {
        List<Ride> unfiltered = rideRepository.findByDriverId(driverId);

        return unfiltered.stream()
                .filter(r -> {
                    LocalDateTime started = r.getStartedAt();
                    if (started == null) return false;

                    boolean matchesStart = (start == null) || !started.isBefore(start);

                    boolean matchesEnd = (end == null) || !started.isAfter(end);

                    return matchesStart && matchesEnd;
                })
                .sorted((ride1, ride2) -> {
                    if (sort == Sorting.DESC) {
                        return ride2.getStartedAt().compareTo(ride1.getStartedAt());
                    } else {
                        return ride1.getStartedAt().compareTo(ride2.getStartedAt());
                    }
                })
                .collect(Collectors.toList());
    }

    public boolean existsOverlappingRide(
            Long driverId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        List<Ride> rides = rideRepository.findByDriverId(
                driverId
        );

        for (Ride ride : rides) {
            LocalDateTime rideStart = ride.getRideRequest().getScheduledTime();
            if (rideStart == null) {
                rideStart = ride.getStartedAt();
            }
            if (rideStart == null) {
                continue;
            }

            LocalDateTime rideEnd = ride.getFinishedAt();
            if (rideEnd == null) {
                rideEnd = rideStart.plusMinutes(
                        routeService.estimateDurationInMinutes(
                                ride.getRideRequest().getRideRoute().getTotalDistanceKm()
                        )
                );
            }

            if (rideStart.isBefore(end) && rideEnd.isAfter(start)) {
                return true;
            }
        }

        return false;
    }

    public List<Ride> findActiveRides(Long driverId) {
        List<Ride> rides = rideRepository.findByDriverId(driverId);

        return rides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.ONGOING)
                .toList();
    }

    public LocalDateTime estimateRideEndTime(Ride ride) {
        LocalDateTime start
                = ride.getStartedAt() != null
                ? ride.getStartedAt()
                : ride.getRideRequest().getScheduledTime();

        if (start == null) {
            return null;
        }

        long durationMinutes
                = routeService.estimateDurationInMinutes(
                        ride.getRideRequest().getRideRoute().getTotalDistanceKm()
                );

        return start.plusMinutes(durationMinutes);
    }

    public boolean isDriverFreeForRide(Driver driver, RideRequest req) {

        LocalDateTime start
                = req.getScheduleType() == ScheduleType.LATER
                ? req.getScheduledTime()
                : LocalDateTime.now();

        LocalDateTime end = start.plusMinutes(
                routeService.estimateDurationInMinutes(req.getRideRoute().getTotalDistanceKm())
        );

        return !this.existsOverlappingRide(
                driver.getId(),
                start,
                end
        );
    }

    public Ride findActiveRide(Long userId) {
        List<Ride> rides = rideRepository.findByDriverId(userId);

        return rides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.ONGOING)
                .findFirst()
                .orElse(null);
    }

    public Ride getOngoingRide(Long userId) {
        List<Ride> ongoingRides = rideRepository.findByStatus(RideStatus.ONGOING);

        return ongoingRides.stream()
                .filter(ride -> ride.getRideRequest().getCreator().getId() == userId)
                .findFirst()
                .orElse(null);
    }

    public void startRide(long rideId, long driverId, List<RoutePointDto> routePoints) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        ride.setStartedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.ONGOING);
        rideRepository.save(ride);
    }

    public void startScheduleRide(long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        ride.setStartedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.ONGOING);
        rideRepository.save(ride);

        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.BUSY);
    }

    public ActiveRideDto getActiveRide(Long userId) {
        Ride ride = getOngoingRide(userId);
        if (ride == null) {
            return null;
        }
        ActiveRideDto dto = new ActiveRideDto();
        dto.setRideId(ride.getId());
        dto.setStatus(ride.getStatus());
        dto.setRoutePoints(
                ride.getRideRequest().getRideRoute().getRoutePoints().stream().map(RoutePoint::toDto).toList()
        );
        dto.setDriverId(ride.getDriver().getId());
        dto.setDriverName(ride.getDriver().getUser().getPerson().getFirstName() + " " + ride.getDriver().getUser().getPerson().getLastName());

        return dto;
    }

    public Ride findById(Long rideId) {
        return rideRepository.findById(rideId).orElseThrow();
    }

    public void save(Ride ride) {
        rideRepository.save(ride);
    }

}
