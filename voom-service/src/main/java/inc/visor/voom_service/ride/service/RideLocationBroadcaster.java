package inc.visor.voom_service.ride.service;

import inc.visor.voom_service.ride.dto.RideLocationDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RideLocationBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    private final int UPDATE_RATE = 500;

    private final RideLocationDto activeRide = new RideLocationDto(); // placeholder


    public RideLocationBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = UPDATE_RATE)
    public void broadcastRideLocation() {

        // we will also use simulation here with actual ride waypoints for update

        messagingTemplate.convertAndSend(
                "/topic/rides/" + activeRide.getRideId() + "/location",
                activeRide
        );
    }

}
