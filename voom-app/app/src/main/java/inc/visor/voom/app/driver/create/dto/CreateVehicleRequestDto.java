package inc.visor.voom.app.driver.create.dto;

public class CreateVehicleRequestDto {

    private String model;
    private String vehicleType;
    private String licensePlate;
    private Integer numberOfSeats;
    private boolean babySeat;
    private boolean petFriendly;

    public CreateVehicleRequestDto(
            String model,
            String vehicleType,
            String licensePlate,
            Integer numberOfSeats,
            boolean babyTransportAllowed,
            boolean petTransportAllowed
    ) {
        this.model = model;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.numberOfSeats = numberOfSeats;
        this.babySeat = babyTransportAllowed;
        this.petFriendly = petTransportAllowed;
    }
}
