package inc.visor.voom.app.admin.driver_approve.dto;


public class DriverVehicleChangeRequestDto {

    private int id;
    private int driverId;
    private String driverFullName;
    private String model;
    private String vehicleType;
    private String licensePlate;
    private int numberOfSeats;
    private boolean babySeat;
    private boolean petFriendly;
    private String status;
    private String createdAt;

    public int getId() { return id; }
    public String getDriverFullName() { return driverFullName; }
    public String getModel() { return model; }
    public String getVehicleType() { return vehicleType; }
    public String getLicensePlate() { return licensePlate; }
    public int getNumberOfSeats() { return numberOfSeats; }
    public boolean isBabySeat() { return babySeat; }
    public boolean isPetFriendly() { return petFriendly; }
}
