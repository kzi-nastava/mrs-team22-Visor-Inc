package inc.visor.voom_service.ride.dto;

import java.time.LocalDateTime;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;

public class RideRequestResponseDto {

    private long requestId;
    private RideRequestStatus status;
    private double distanceKm;
    private double price;
    private LocalDateTime scheduledTime;
    private DriverSummaryDto driver;

    private RideRequestResponseDto(
            long requestId,
            RideRequestStatus status,
            double distanceKm,
            double price,
            LocalDateTime scheduledTime,
            DriverSummaryDto driver
    ) {
        this.requestId = requestId;
        this.status = status;
        this.distanceKm = distanceKm;
        this.price = price;
        this.scheduledTime = scheduledTime;
        this.driver = driver;
    }

    public static RideRequestResponseDto create(
            long requestId,
            RideRequestStatus status,
            double distanceKm,
            double price,
            LocalDateTime scheduledTime,
            DriverSummaryDto driver
    ) {
        return new RideRequestResponseDto(
                requestId,
                status,
                distanceKm,
                price,
                scheduledTime,
                driver
        );
    }

    public static RideRequestResponseDto from(
            RideRequest rideRequest,
            double distanceKm,
            DriverSummaryDto driver
    ) {
        return new RideRequestResponseDto(
                rideRequest.getId(),
                rideRequest.getStatus(),
                distanceKm,
                rideRequest.getCalculatedPrice(),
                rideRequest.getScheduledTime(),
                driver
        );
    }

    public long getRequestId() {
        return requestId;
    }

    public RideRequestStatus getStatus() {
        return status;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public DriverSummaryDto getDriver() {
        return driver;
    }

}
