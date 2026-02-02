package inc.visor.voom_service.complaints.service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.complaints.model.Complaint;
import inc.visor.voom_service.complaints.repository.ComplaintRepository;
import inc.visor.voom_service.ride.service.RideService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final RideService rideService;
    private final UserService userService;
    private final DriverService driverService;

    @Transactional
    public void reportRide(Long rideId, User user, String message) {
        final Complaint report = new Complaint();

        final Ride ride = rideService.getRide(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found: " + rideId));

        report.setReporter(user);
        report.setRide(ride);
        report.setMessage(message);

        complaintRepository.save(report);
    }

    public List<Complaint> getByRide(Long rideId) {
        rideService.getRide(rideId).orElseThrow(() -> new EntityNotFoundException("Ride not found with id: " + rideId));
        return complaintRepository.findByRideId(rideId);
    }

    public List<Complaint> getByDriver(Long driverId) {
        driverService.getDriver(driverId).orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId));
        return complaintRepository.findByRide_Driver_Id(driverId);
    }
}

