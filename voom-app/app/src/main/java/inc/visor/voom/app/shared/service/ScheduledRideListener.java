package inc.visor.voom.app.shared.service;

import inc.visor.voom.app.shared.dto.ScheduledRideDto;

public interface ScheduledRideListener {
    void onScheduledRides(ScheduledRideDto[] rides);
}

