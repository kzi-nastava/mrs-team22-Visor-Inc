package inc.visor.voom.app.shared.service;

import android.util.Log;

import com.google.gson.Gson;

import inc.visor.voom.app.config.AppConfig;
import inc.visor.voom.app.driver.api.DriverMetaProvider;
import inc.visor.voom.app.driver.dto.DriverAssignedDto;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.shared.dto.DriverPositionDto;
import inc.visor.voom.app.shared.dto.ScheduledRideDto;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;


public class DriverSimulationWsService {

    private final DriverSimulationManager simulationManager;
    private final DriverMetaProvider metaProvider;
    private StompClient stompClient;
    private final DriverAssignmentListener assignmentListener;
    private final ScheduledRideListener scheduledRideListener;

    public interface OnPositionReceived {
        void onUpdate(DriverPositionDto dto);
    }

    private OnPositionReceived positionReceivedListener;

    public void setOnPositionReceivedListener(OnPositionReceived listener) {
        this.positionReceivedListener = listener;
    }


    public DriverSimulationWsService(
            DriverSimulationManager simulationManager,
            DriverMetaProvider metaProvider
    ) {
        this(simulationManager, metaProvider, null, null);
    }

    public DriverSimulationWsService(
            DriverSimulationManager simulationManager,
            DriverMetaProvider metaProvider,
            DriverAssignmentListener assignmentListener,
            ScheduledRideListener scheduleListener
    ) {
        this.simulationManager = simulationManager;
        this.metaProvider = metaProvider;
        this.assignmentListener = assignmentListener;
        this.scheduledRideListener = scheduleListener;
    }

    public void connect() {

        String wsUrl = AppConfig.getWsUrl();

        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                wsUrl
        );

        stompClient.lifecycle().subscribe(lifecycleEvent -> {

            switch (lifecycleEvent.getType()) {

                case OPENED:
                    Log.d("WS", "Connected");
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

                    DriverSummaryDto meta = null;

                    if (positionReceivedListener != null) positionReceivedListener.onUpdate(dto);

                    if (metaProvider != null) {
                        meta = metaProvider.findActiveDriver((int) dto.driverId);
                    }

                    simulationManager.updateDriverPosition(
                            dto.driverId,
                            dto.lat,
                            dto.lng,
                            meta
                    );
                });

        stompClient.topic("/topic/driver-assigned")
                .subscribe(topicMessage -> {
                    DriverAssignedDto dto =
                            new Gson().fromJson(
                                    topicMessage.getPayload(),
                                    DriverAssignedDto.class
                            );
                    if (assignmentListener != null) {
                        assignmentListener.onDriverAssigned(dto);
                    } else {
                    }

                }, throwable -> {
                    Log.e("WS_ASSIGN", "Assigned topic error", throwable);
                });

        stompClient.topic("/topic/scheduled-rides")
                .subscribe(topicMessage -> {

                    ScheduledRideDto[] rides =
                            new Gson().fromJson(
                                    topicMessage.getPayload(),
                                    ScheduledRideDto[].class
                            );

                    if (scheduledRideListener != null) {
                        scheduledRideListener.onScheduledRides(rides);
                    }

                }, throwable -> {
                    Log.e("WS_SCHEDULE", "Scheduled rides topic error", throwable);
                });



    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }
}
