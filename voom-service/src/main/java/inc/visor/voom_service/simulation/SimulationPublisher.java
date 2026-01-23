package inc.visor.voom_service.simulation;

import inc.visor.voom_service.osrm.dto.LatLng;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SimulationPublisher {

    private record LocationDto(long driverId, double lat, double lng, boolean finished) {}

    private final SimpMessagingTemplate messaging;

    public SimulationPublisher(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    public void publishPosition(long driverId, LatLng pos, boolean finished) {
        System.out.println("sending");
        messaging.convertAndSend(
                "/topic/drivers-positions",
                new LocationDto(driverId, pos.lat(), pos.lng(), finished)
        );
    }
}

