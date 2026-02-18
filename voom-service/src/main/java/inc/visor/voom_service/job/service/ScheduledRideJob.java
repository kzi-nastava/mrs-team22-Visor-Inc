package inc.visor.voom_service.job.service;

import inc.visor.voom_service.osrm.dto.ScheduledRideDto;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.repository.RideRequestRepository;
import inc.visor.voom_service.shared.RoutePointDto;
import inc.visor.voom_service.shared.notification.model.enums.NotificationType;
import inc.visor.voom_service.shared.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledRideJob {

    private final RideRequestRepository rideRequestRepository;
    private final RideRepository rideRepository;
    private final RideWsService rideWsService;
    private final NotificationService notificationService;

    public ScheduledRideJob(
            RideRequestRepository rideRequestRepository,
            RideRepository rideRepository,
            RideWsService rideWsService,
            NotificationService notificationService
    ) {
        this.rideRequestRepository = rideRequestRepository;
        this.rideRepository = rideRepository;
        this.rideWsService = rideWsService;
        this.notificationService = notificationService;
    }

    @Transactional()
    @Scheduled(fixedRate = 5_000)
    public void processScheduledRides() {

        System.out.println("Processing scheduled rides...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(20);

        List<RideRequest> upcomingRequests
                = rideRequestRepository.findUpcomingScheduled(now, threshold);

        System.out.println("Found " + upcomingRequests + " upcoming scheduled rides.");

        List<ScheduledRideDto> payload = new ArrayList<>();

        for (RideRequest req : upcomingRequests) {

            Ride ride = rideRepository.findByRideRequestId(req.getId()).orElse(null);

            if (ride == null) {
                continue;
            }

            if (ride.getStatus() != RideStatus.SCHEDULED) {
                continue;
            }

            if (!ride.isReminderSent()) {

                notificationService.createAndSendNotification(
                        req.getCreator(),
                        NotificationType.SCHEDULE_REMINDER,
                        "Upcoming Ride Reminder",
                        "Your scheduled ride is starting soon.",
                        ride.getId()
                );

                notificationService.createAndSendNotification(
                        ride.getDriver().getUser(),
                        NotificationType.SCHEDULE_REMINDER,
                        "Upcoming Ride Reminder",
                        "A scheduled ride you are assigned to is starting soon. Pick up location: " + req.getRideRoute().getRoutePoints().get(0).getAddress(),
                        ride.getId()
                );
                ride.setReminderSent(true);
                rideRepository.save(ride);
            }

            ScheduledRideDto dto = new ScheduledRideDto();
            dto.rideId = ride.getId();
            dto.rideRequestId = req.getId();
            dto.driverId = ride.getDriver() != null ? ride.getDriver().getId() : null;
            dto.scheduledStartTime = req.getScheduledTime();
            dto.status = ride.getStatus().name();
            dto.creatorId = ride.getRideRequest().getCreator().getId();
            dto.route = req.getRideRoute().getRoutePoints().stream()
                    .map(p -> {
                        RoutePointDto rp = new RoutePointDto();
                        rp.setLat(p.getLatitude());
                        rp.setLng(p.getLongitude());
                        rp.setOrderIndex(p.getOrderIndex());
                        rp.setType(p.getPointType());
                        rp.setAddress(p.getAddress());
                        return rp;
                    })
                    .toList();

            payload.add(dto);
        }

        rideWsService.sendScheduledRides(payload);

    }

}
