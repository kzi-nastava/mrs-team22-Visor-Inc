package inc.visor.voom_service.driver.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverVehicleChangeRequestDto {

    private Long id;
    private Long driverId;
    private String driverFullName;

    private String model;
    private String vehicleType;
    private String licensePlate;
    private int numberOfSeats;
    private boolean babySeat;
    private boolean petFriendly;

    private String status;
    private LocalDateTime createdAt;
}
