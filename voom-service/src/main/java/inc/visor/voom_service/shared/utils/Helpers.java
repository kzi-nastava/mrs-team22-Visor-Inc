package inc.visor.voom_service.shared.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import inc.visor.voom_service.ride.dto.RideRequestCreateDTO.DriverLocationDTO;

public class Helpers {
    private Helpers() {}

    public static Map<Long, DriverLocationDTO> snapshotToMap(
            List<DriverLocationDTO> snapshot
    ) {
        return snapshot.stream()
                .collect(Collectors.toMap(
                        s -> s.driverId,
                        s -> s
                ));
    }

}
