package inc.visor.voom_service.shared.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import inc.visor.voom_service.ride.dto.RideRequestCreateDto;

public class Helpers {
    private Helpers() {}

    public static Map<Long, RideRequestCreateDto.DriverLocationDto> snapshotToMap(
            List<RideRequestCreateDto.DriverLocationDto> snapshot
    ) {
        return snapshot.stream()
                .collect(Collectors.toMap(
                        s -> s.driverId,
                        s -> s
                ));
    }

}
