package inc.visor.voom_service.ride.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.mail.EmailService;
import inc.visor.voom_service.ride.dto.ActiveRideDto;
import inc.visor.voom_service.ride.dto.RideLocationDto;
import static inc.visor.voom_service.ride.helpers.RideHistoryFormatter.formatAddress;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.model.enums.Sorting;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.route.service.RideRouteService;
import inc.visor.voom_service.shared.RoutePointDto;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RideRouteService routeService;
    private final EmailService emailService;


    public RideService(RideRepository rideRepository, RideRouteService routeService, EmailService emailService) {
        this.rideRepository = rideRepository;
        this.routeService = routeService;
        this.emailService = emailService;
    }

    public void updateRidePosition(RideLocationDto dto) {
        // get and set position
        return;
    }

    public void finishRide(Ride ride) {
        // set status
        return;
    }

    public Optional<Ride> getRide(long rideId) {
        return this.rideRepository.findById(rideId);
    }

    public List<Ride> getRides() {
        return this.rideRepository.findAll();
    }

    public Ride update(Ride ride) {
        return this.rideRepository.save(ride);
    }

    public List<Ride> getDriverRides(Long driverId, LocalDateTime start, LocalDateTime end, Sorting sort) {
        List<Ride> unfiltered = rideRepository.findByDriverId(driverId);

        return getRidesFilteredSortedByDate(start, end, sort, unfiltered);
    }

    public List<Ride> getRidesFilteredSortedByDate(LocalDateTime start, LocalDateTime end, Sorting sort, List<Ride> unfiltered) {
        return unfiltered.stream()
                .filter(r -> {
                    LocalDateTime started = r.getStartedAt();
                    if (started == null) return false;

                    boolean matchesStart = (start == null) || !started.isBefore(start);

                    boolean matchesEnd = (end == null) || !started.isAfter(end);

                    return matchesStart && matchesEnd;
                })
                .sorted((r1, r2) -> {
                    Comparator<Ride> dateComparator = Comparator.comparing(
                            Ride::getStartedAt,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    );

                    if (sort == Sorting.DESC) {
                        return dateComparator.reversed().compare(r1, r2);
                    }
                    return dateComparator.compare(r1, r2);
                })
                .collect(Collectors.toList());
    }

    public List<Ride> getUserRides(long userId, LocalDateTime start, LocalDateTime end, Sorting sort) {
        List<Ride> unfiltered = rideRepository.findByRideRequest_Creator_Id(userId);

        return getRidesFilteredSortedByDate(start, end, sort, unfiltered);
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
                .filter(ride -> ride.getStatus() == RideStatus.ONGOING || ride.getStatus() == RideStatus.STARTED)
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
        List<Ride> rides = rideRepository.findByDriver_User_Id(userId);

        return rides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.ONGOING || ride.getStatus() == RideStatus.STARTED)
                .findFirst()
                .orElse(null);
    }

    public Ride getOngoingRide(Long userId) {

        List<Ride> ongoingRides = rideRepository.findByStatusIn(List.of(RideStatus.ONGOING, RideStatus.STARTED));

        // this should load from db by status and then filter by user id
        return ongoingRides.stream()
                .filter(ride -> ride.getRideRequest().getCreator().getId() == userId || ride.getPassengers().stream().anyMatch(user -> user.getId() == userId))
                .findFirst()
                .orElse(null);
    }

    public void startRide(long rideId, long driverId, List<RoutePointDto> routePoints) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();

        ride.setStartedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.STARTED);
        rideRepository.save(ride);

        String pickupAddress = formatAddress(ride.getPickupAddress());
        String dropoffAddress = formatAddress(ride.getDropoffAddress());
        String address = pickupAddress + " - " + dropoffAddress;

        String trackingUrl = "http://localhost:4200/user/ride/tracking";

        String creatorEmail = ride.getRideRequest().getCreator().getEmail();
        emailService.sendRideTrackingLink(
                creatorEmail,
                address,
                trackingUrl
        );

        for (String email : ride.getRideRequest().getLinkedPassengerEmails()) {
            emailService.sendRideTrackingLink(
                    email,
                    address,
                    trackingUrl
            );
        }


    }

    public void finishRide(long rideId) {
        Ride ride = findById(rideId);
        ride.setFinishedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);

        String pickupAddress = formatAddress(ride.getPickupAddress());
        String dropoffAddress = formatAddress(ride.getDropoffAddress());
        String address = pickupAddress + " - " + dropoffAddress;

        rideRepository.save(ride);

        String creatorEmail = ride.getRideRequest().getCreator().getEmail();
        emailService.sendRideCompletionEmail(
                creatorEmail,
                address
        );

        for (String email : ride.getRideRequest().getLinkedPassengerEmails()) {
            emailService.sendRideCompletionEmail(
                    email,
                    address
            );
        }
    }

    public void startScheduleRide(long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        ride.setStartedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.ONGOING);
        rideRepository.save(ride);

        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.BUSY);
    }

    public ActiveRideDto getActiveRide(User user) {
        Ride activeRide = new Ride();

        if (user.getUserRole().getId() == 2) {
            activeRide = findActiveRide(user.getId());
        } else {
            activeRide = getOngoingRide(user.getId());
        }

        if (activeRide == null) {
            return null;
        }
        ActiveRideDto dto = new ActiveRideDto();
        dto.setRideId(activeRide.getId());
        dto.setStatus(activeRide.getStatus());
        dto.setRoutePoints(
                activeRide.getRideRequest().getRideRoute().getRoutePoints().stream().map(RoutePoint::toDto).toList()
        );
        dto.setDriverId(activeRide.getDriver().getId());
        dto.setDriverName(activeRide.getDriver().getUser().getPerson().getFirstName() + " " + activeRide.getDriver().getUser().getPerson().getLastName());

        return dto;
    }


    public Ride findById(Long rideId) {
        return rideRepository.findById(rideId).orElseThrow();
    }

    public void save(Ride ride) {
        rideRepository.save(ride);
    }

    public List<Ride> getScheduledRides(RideStatus rideStatus) {
        return this.rideRepository.findByStatus(rideStatus);
    }
}
