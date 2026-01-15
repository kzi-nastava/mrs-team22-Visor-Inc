package inc.visor.voom_service.job.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.osrm.dto.ScheduledRideDto;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.repository.RideRequestRepository;
import inc.visor.voom_service.shared.RoutePointDto;
import jakarta.transaction.Transactional;

@Service
public class ScheduledRideJob {

    private final RideRequestRepository rideRequestRepository;
    private final RideRepository rideRepository;
    private final RideWsService rideWsService;

    public ScheduledRideJob(
            RideRequestRepository rideRequestRepository,
            RideRepository rideRepository,
            RideWsService rideWsService
    ) {
        this.rideRequestRepository = rideRequestRepository;
        this.rideRepository = rideRepository;
        this.rideWsService = rideWsService;
    }

    @Transactional()
    @Scheduled(fixedRate = 5_000)
    public void processScheduledRides() {

        System.out.println("Processing scheduled rides...");

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime threshold = now.plusMinutes(20);

        List<RideRequest> upcomingRequests
                = rideRequestRepository.findUpcomingScheduled(now, threshold);

        System.out.println("Found " + upcomingRequests + " upcoming scheduled rides.");

        List<ScheduledRideDto> payload = new ArrayList<>();

        for (RideRequest req : upcomingRequests) {
            rideRepository.findByRideRequestId(req.getId()).ifPresent(ride -> {

                ScheduledRideDto dto = new ScheduledRideDto();
                dto.rideId = ride.getId();
                dto.rideRequestId = req.getId();
                dto.driverId = ride.getDriver() != null ? ride.getDriver().getId() : null;
                dto.scheduledStartTime = req.getScheduledTime();
                dto.status = ride.getStatus().name();

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
            });
        }

        rideWsService.sendScheduledRides(payload);

    }

}
