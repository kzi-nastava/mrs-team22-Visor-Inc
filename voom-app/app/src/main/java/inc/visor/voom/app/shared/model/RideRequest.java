package inc.visor.voom.app.shared.model;

import java.util.List;

import inc.visor.voom.app.shared.model.enums.RideRequestStatus;
import inc.visor.voom.app.shared.model.enums.ScheduleType;

public class RideRequest {
    public Long id;
    public User creator;
    public RideRoute rideRoute;
    public RideRequestStatus status;
    public ScheduleType scheduleType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public RideRoute getRideRoute() {
        return rideRoute;
    }

    public void setRideRoute(RideRoute rideRoute) {
        this.rideRoute = rideRoute;
    }

    public RideRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RideRequestStatus status) {
        this.status = status;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
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

    public double getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(double calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public List<String> getLinkedPassengerEmails() {
        return linkedPassengerEmails;
    }

    public void setLinkedPassengerEmails(List<String> linkedPassengerEmails) {
        this.linkedPassengerEmails = linkedPassengerEmails;
    }

    public User getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(User cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String scheduledTime;
    public VehicleType vehicleType;
    public boolean babyTransport;
    public boolean petTransport;
    public double calculatedPrice;
    public List<String> linkedPassengerEmails;
    public User cancelledBy;
}
