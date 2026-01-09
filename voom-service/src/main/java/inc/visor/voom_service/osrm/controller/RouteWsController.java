package inc.visor.voom_service.osrm.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.dto.RouteRequestDto;
import inc.visor.voom_service.osrm.dto.RouteResponseDto;
import inc.visor.voom_service.osrm.service.OsrmQueueService;
import inc.visor.voom_service.osrm.service.OsrmService;

@Controller
public class RouteWsController {

    private final OsrmQueueService queue;
    private final OsrmService osrm;
    private final SimpMessagingTemplate messaging;

    public RouteWsController(
            OsrmQueueService queue,
            OsrmService osrm,
            SimpMessagingTemplate messaging
    ) {
        this.queue = queue;
        this.osrm = osrm;
        this.messaging = messaging;
    }

    @MessageMapping("/route")
    public void route(RouteRequestDto req) {
        queue.submit(() -> {
            List<LatLng> route = osrm.getRoute(req.start(), req.end());

            messaging.convertAndSend(
                    "/topic/route",
                    new RouteResponseDto(req.driverId(), route)
            );
        });
    }

}
