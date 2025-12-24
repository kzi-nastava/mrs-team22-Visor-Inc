package inc.visor.voom_service.ride.dto;

import java.time.LocalDateTime;

import inc.visor.voom_service.auth.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;

public class RideRequestResponseDto {

    private Long id;
    private RideRequestStatus status;
    private double price;
    private LocalDateTime scheduledTime;

    private DriverSummaryDto driver;



    public RideRequestResponseDto() {
    }

    public RideRequestResponseDto(Long id, RideRequestStatus status, double price, LocalDateTime scheduledTime, DriverSummaryDto driver) {
        this.id = id;
        this.status = status;
        this.price = price;
        this.scheduledTime = scheduledTime;
        this.driver = driver;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RideRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RideRequestStatus status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public DriverSummaryDto getDriver() {
        return driver;
    }

    public void setDriver(DriverSummaryDto driver) {
        this.driver = driver;
    }
}
