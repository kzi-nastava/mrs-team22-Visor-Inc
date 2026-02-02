package inc.visor.voom.app.driver.api;

import inc.visor.voom.app.driver.dto.DriverSummaryDto;

public interface DriverMetaProvider {
    DriverSummaryDto findActiveDriver(int id);
}