package inc.visor.voom_service.ride.stop.data;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.service.RideRequestService;
import inc.visor.voom_service.vehicle.model.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RideRequestData {

    RideRequestService rideRequestService;

    @Autowired
    public RideRequestData(RideRequestService rideRequestService) {
        this.rideRequestService = rideRequestService;
    }

    public class RideRequestDataBuilder {
        private final RideRequestData rideRequestData;
        private final RideRequest rideRequest;

        private RideRequestDataBuilder(RideRequestData rideRequestData) {
            this.rideRequest = new RideRequest();
            this.rideRequest.setStatus(RideRequestStatus.PENDING); // Default status
            this.rideRequest.setScheduleType(ScheduleType.NOW); // Default schedule type
            this.rideRequest.setBabyTransport(false);
            this.rideRequest.setPetTransport(false);
            this.rideRequest.setCalculatedPrice(0.0);
            this.rideRequest.setLinkedPassengerEmails(new ArrayList<>());
            this.rideRequestData = rideRequestData;
        }

        public RideRequestDataBuilder withCreator(User creator) {
            rideRequest.setCreator(creator);
            return this;
        }

        public RideRequestDataBuilder withRideRoute(RideRoute rideRoute) {
            rideRequest.setRideRoute(rideRoute);
            return this;
        }

        public RideRequestDataBuilder withStatus(RideRequestStatus status) {
            rideRequest.setStatus(status);
            return this;
        }

        public RideRequestDataBuilder withScheduleType(ScheduleType scheduleType) {
            rideRequest.setScheduleType(scheduleType);
            return this;
        }

        public RideRequestDataBuilder withScheduledTime(LocalDateTime scheduledTime) {
            rideRequest.setScheduledTime(scheduledTime);
            return this;
        }

        public RideRequestDataBuilder withVehicleType(VehicleType vehicleType) {
            rideRequest.setVehicleType(vehicleType);
            return this;
        }

        public RideRequestDataBuilder withBabyTransport(boolean babyTransport) {
            rideRequest.setBabyTransport(babyTransport);
            return this;
        }

        public RideRequestDataBuilder withPetTransport(boolean petTransport) {
            rideRequest.setPetTransport(petTransport);
            return this;
        }

        public RideRequestDataBuilder withCalculatedPrice(double calculatedPrice) {
            rideRequest.setCalculatedPrice(calculatedPrice);
            return this;
        }

        public RideRequestDataBuilder withLinkedPassengerEmails(List<String> linkedPassengerEmails) {
            rideRequest.setLinkedPassengerEmails(linkedPassengerEmails);
            return this;
        }

        public RideRequestDataBuilder addLinkedPassengerEmail(String email) {
            if (rideRequest.getLinkedPassengerEmails() == null) {
                rideRequest.setLinkedPassengerEmails(new ArrayList<>());
            }
            rideRequest.getLinkedPassengerEmails().add(email);
            return this;
        }

        public RideRequestDataBuilder withCancelledBy(User cancelledBy) {
            rideRequest.setCancelledBy(cancelledBy);
            return this;
        }

        public RideRequestDataBuilder withReason(String reason) {
            rideRequest.setReason(reason);
            return this;
        }

        public RideRequest save() {
            return rideRequest;
        }
}
}
