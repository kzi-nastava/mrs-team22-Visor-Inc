package inc.visor.voom_service.ride.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.complaints.dto.ComplaintSummaryDto;
import inc.visor.voom_service.rating.dto.RatingSummaryDto;
import inc.visor.voom_service.rating.model.Rating;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideHistoryDto {
    private Long id;
    private RideStatus status;
    private RideRequest rideRequest;
    private RideRoute rideRoute;
    private DriverSummaryDto driver;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private List<User> passengers;
    private User cancelledBy;
    private List<ComplaintSummaryDto> complaints;
    private List<RatingSummaryDto> ratings;

    public RideHistoryDto(Ride ride) {
        this.id = ride.getId();
        this.status = ride.getStatus();
        this.rideRequest = ride.getRideRequest();
        this.rideRoute = ride.getRideRequest().getRideRoute();
        this.driver = new DriverSummaryDto(ride.getDriver());
        this.startedAt = ride.getStartedAt();
        this.finishedAt = ride.getFinishedAt();
        this.passengers = ride.getPassengers();
        this.cancelledBy = ride.getRideRequest().getCancelledBy();
        this.complaints = ride.getComplaints().stream().map(ComplaintSummaryDto::new).collect(Collectors.toList());
        this.ratings = ride.getRatings().stream().map(RatingSummaryDto::new).collect(Collectors.toList());
    }

    public RideHistoryDto() {
    }
}
