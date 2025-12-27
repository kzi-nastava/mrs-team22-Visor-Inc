package inc.visor.voom_service.driver.service;

import inc.visor.voom_service.driver.dto.DriverLocationDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DriverLocationBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final int UPDATE_RATE = 1000;


//    list of drivers locations that we can use for simulation
//    private final List<DriverLocationDto> drivers = new ArrayList<>();


    public DriverLocationBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @Scheduled(fixedRate = UPDATE_RATE)
    public void broadcast() {
        // when simulating we can use for loop
        // we are updating one by one driver position which is closer to real world cuz some would be stuck in traffic or making pitstops
        // for driver :  drivers { updatePosition() + convertAndSend() }
        DriverLocationDto dto = new DriverLocationDto();
        messagingTemplate.convertAndSend("/topic/drivers/locations", dto);
    }



}

