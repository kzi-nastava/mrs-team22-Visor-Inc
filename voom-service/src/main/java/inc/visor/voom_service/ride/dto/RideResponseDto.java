package inc.visor.voom_service.ride.dto;

import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RideResponseDto {

    private Long id;
    private RideStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private String driverName;
    private String passengerName;

    private Long driverId;

    private List<String> passengerNames;

    private String startAddress;
    private String destinationAddress;

    public RideResponseDto() {
    }

    public RideResponseDto(Long id, RideStatus status, LocalDateTime startedAt, LocalDateTime finishedAt, String driverName, String passengerName, Long driverId, String startAddress, String destinationAddress) {
        this.id = id;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.driverName = driverName;
        this.passengerName = passengerName;
        this.driverId = driverId;
        this.startAddress = startAddress;
        this.destinationAddress = destinationAddress;
    }

    public RideResponseDto(
            Long id,
            RideStatus status,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            String driverName,
            String passengerName
    ) {
        this.id = id;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.driverName = driverName;
        this.passengerName = passengerName;
    }

    public RideResponseDto(Long id, RideStatus status, LocalDateTime startedAt, LocalDateTime finishedAt, String driverName, String passengerName, List<String> passengerNames, String startAddress, String destinationAddress) {
        this.id = id;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.driverName = driverName;
        this.passengerName = passengerName;
        this.passengerNames = passengerNames;
        this.startAddress = startAddress;
        this.destinationAddress = destinationAddress;
    }

    public RideResponseDto(Ride ride) {
        this.id = ride.getId();
        this.driverId = ride.getDriver().getId();
        this.status = ride.getStatus();
        this.startedAt = ride.getStartedAt();
        this.finishedAt = ride.getFinishedAt();
        this.driverName = ride.getDriver().getUser().getPerson().getFirstName();
        this.passengerName = ride.getRideRequest().getCreator().getPerson().getFirstName();
        this.passengerNames = ride.getPassengers().stream().map(user -> user.getPerson().getFirstName()).toList();
        this.startAddress = ride.getRideRequest().getRideRoute().getPickupPoint().getAddress();
        this.destinationAddress = ride.getRideRequest().getRideRoute().getDropoffPoint().getAddress();
    }
}
