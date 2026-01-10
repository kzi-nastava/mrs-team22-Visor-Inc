package inc.visor.voom_service.osrm.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.osrm.dto.ScheduledRideDto;

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
}
