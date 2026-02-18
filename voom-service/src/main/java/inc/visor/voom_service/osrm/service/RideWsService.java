package inc.visor.voom_service.osrm.service;

import inc.visor.voom_service.osrm.dto.DriverAssignedDto;
import inc.visor.voom_service.osrm.dto.ScheduledRideDto;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RideWsService {

    private final SimpMessagingTemplate messaging;

    public RideWsService(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    public void sendScheduledRides(List<ScheduledRideDto> rides) {
        messaging.convertAndSend(
                "/topic/scheduled-rides",
                rides
        );
    }

    public void sendDriverAssigned(DriverAssignedDto dto) {
        messaging.convertAndSend(
                "/topic/driver-assigned",
                dto
        );
    }

    public void sendRideChanges(RideResponseDto dto) {
        messaging.convertAndSend("/topic/ride-changes", dto);
    }

    public void sendRidePanic(RideResponseDto dto) {
        log.info("PANIC", dto.getId());
        messaging.convertAndSend("/topic/ride-panic", dto);
    }

}
