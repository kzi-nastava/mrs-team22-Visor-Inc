package inc.visor.voom.app.shared.service;

import inc.visor.voom.app.shared.dto.ride.RideResponseDto;

public interface PanicListener {
    void onRidePanic(RideResponseDto dto);
}
