package inc.visor.voom_service.vehicle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleDto {
    private long vehicleTypeId;
    private long driverId;
    private int year;
    private String model;
    private String licensePlate;
    private boolean babySeat;
    private boolean petFriendly;
    private int numberOfSeats;

    public CreateVehicleDto() {
    }
}
