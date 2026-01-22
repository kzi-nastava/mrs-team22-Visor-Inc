package inc.visor.voom.app.shared.service;

import android.util.Log;

import com.google.gson.Gson;

import inc.visor.voom.app.shared.dto.DriverPositionDto;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class DriverSimulationWsService {

    private final DriverSimulationManager simulationManager;
    private StompClient stompClient;

    public DriverSimulationWsService(DriverSimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }

    public void connect() {

        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "ws://192.168.1.6:8080/ws"
        );

        stompClient.lifecycle().subscribe(lifecycleEvent -> {

            switch (lifecycleEvent.getType()) {

                case OPENED:
                    Log.d("WS", "Connected");

                    stompClient.topic("/topic/drivers-positions")
                            .subscribe(topicMessage -> {
                                // handle message
                            }, throwable -> {
                                Log.e("WS", "Topic error", throwable);
                            });

                    break;

                case ERROR:
                    Log.e("WS", "Connection error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d("WS", "Connection closed");
                    break;
            }
        });

        stompClient.connect();

        stompClient.topic("/topic/drivers-positions")
                .subscribe(topicMessage -> {

                    DriverPositionDto dto =
                            new Gson().fromJson(
                                    topicMessage.getPayload(),
                                    DriverPositionDto.class
                            );

                    simulationManager.updateDriverPosition(
                            dto.driverId,
                            dto.lat,
                            dto.lng
                    );
                });
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }
}


