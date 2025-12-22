package inc.visor.voom_service.authorized.ride.dto;

import inc.visor.voom_service.domain.ride.enums.RideRequestStatus;

public class RideRequestResponseDto {
    
    private Long id;
    private RideRequestStatus status;
    private double price;

    public RideRequestResponseDto() {}

    public RideRequestResponseDto(Long id, RideRequestStatus status, double price) {
        this.id = id;
        this.status = status;
        this.price = price;
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
}
