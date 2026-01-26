package inc.visor.voom.app.user.home.dto;

import inc.visor.voom.app.driver.dto.DriverSummaryDto;

public class RideRequestResponseDto {
    public long requestId;
    public String status;
    public double distanceKm;
    public double price;
    public String scheduledTime;

    public DriverSummaryDto driver;

}
