package inc.visor.voom_service.ride.stop.data;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RideData {

    RideService rideService;

    @Autowired
    public RideData(RideService rideService) {
        this.rideService = rideService;
    }

    public class RideDataBuilder {
        private final Ride ride;

        private RideDataBuilder() {
            ride = new Ride();
        }

        public RideDataBuilder withRideRequest(RideRequest rideRequest) {
            ride.setRideRequest(rideRequest);
            return this;
        }

        public RideDataBuilder withDriver(Driver driver) {
            ride.setDriver(driver);
            return this;
        }

        public RideDataBuilder withStatus(RideStatus status) {
            ride.setStatus(status);
            return this;
        }

        public RideDataBuilder withStartedAt(LocalDateTime startedAt) {
            ride.setStartedAt(startedAt);
            return this;
        }

        public RideDataBuilder withFinishedAt(LocalDateTime finishedAt) {
            ride.setFinishedAt(finishedAt);
            return this;
        }

        public RideDataBuilder withReminderSent(boolean reminderSent) {
            ride.setReminderSent(reminderSent);
            return this;
        }

        public RideDataBuilder withPassengers(List<User> passengers) {
            ride.setPassengers(passengers);
            return this;
        }

        public RideDataBuilder addPassenger(User passenger) {
            if (ride.getPassengers() == null) {
                ride.setPassengers(new ArrayList<>());
            }
            ride.getPassengers().add(passenger);
            return this;
        }

        public Ride save() {
            return rideService.save(ride);
        }
    }

}
