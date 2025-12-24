package inc.visor.voom_service.ride.dto;

import java.time.LocalDateTime;
import java.util.List;

import inc.visor.voom_service.ride.model.enums.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateRideRequestDto {
    
    @NotEmpty(message = "Route points are required")
    @Valid
    private List<RoutePointDto> routePoints;

    @NotEmpty(message = "Passenger emails are required")
    private List<String> passengerEmails;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private boolean babyTransport;
    private boolean petTransport;

    private LocalDateTime scheduledTime;

    public List<RoutePointDto> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePointDto> routePoints) {
        this.routePoints = routePoints;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabyTransport() {
        return babyTransport;
    }
    public void setBabyTransport(boolean babyTransport) {
        this.babyTransport = babyTransport;
    }
    public boolean isPetTransport() {
        return petTransport;
    }

    public void setPetTransport(boolean petTransport) {
        this.petTransport = petTransport;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
