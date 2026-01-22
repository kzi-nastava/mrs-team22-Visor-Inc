package inc.visor.voom_service.vehicle.dto;

import inc.visor.voom_service.vehicle.model.Vehicle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleDto {
    private long id;
    private long driverId;
    private long vehicleTypeId;
    private int year;
    private String model;
    private String licensePlate;
    private boolean babySeat;
    private boolean petFriendly;
    private int numberOfSeats;

    public VehicleDto(Vehicle vehicle) {
        id = vehicle.getId();
        driverId = vehicle.getDriver().getId();
        vehicleTypeId = vehicle.getVehicleType().getId();
        year = vehicle.getYear();
        model = vehicle.getModel();
        licensePlate = vehicle.getLicensePlate();
        babySeat = vehicle.isBabySeat();
        petFriendly = vehicle.isPetFriendly();
        numberOfSeats = vehicle.getNumberOfSeats();
    }

    public VehicleDto() {
    }
}
