package inc.visor.voom.app.shared.service;

import inc.visor.voom.app.driver.dto.DriverAssignedDto;

public interface DriverAssignmentListener {
    void onDriverAssigned(DriverAssignedDto dto);
}

