package inc.visor.voom_service.ride.service;

import inc.visor.voom_service.ride.model.RideReport;
import inc.visor.voom_service.ride.repository.RideReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideReportService {

    private final RideReportRepository repository;

    public void reportRide(Long rideId, String message) {
        RideReport report = new RideReport();
        report.setRideId(rideId);
        report.setMessage(message);
        repository.save(report);
    }
}

