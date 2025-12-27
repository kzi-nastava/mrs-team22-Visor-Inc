package inc.visor.voom_service.ride.dto;

import java.time.LocalDateTime;
import java.util.List;

import inc.visor.voom_service.ride.model.enums.RideStatus;

public class RideResponseDto {

    private Long id;
    private RideStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private String driverName;
    private String passengerName;

    private List<String> passengerNames;

    private String startAddress;
    private String destinationAddress;

    public RideResponseDto() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public List<String> getPassengerNames() {
        return passengerNames;
    }

    public void setPassengerNames(List<String> passengerNames) {
        this.passengerNames = passengerNames;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
}
