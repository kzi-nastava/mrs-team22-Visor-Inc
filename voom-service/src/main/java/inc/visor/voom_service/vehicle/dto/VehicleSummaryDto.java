package inc.visor.voom_service.vehicle.dto;
public class VehicleSummaryDto {
    
    private String vehicleType;
    private int year;
    private String model;
    private String licensePlate;
    private boolean babySeat;
    private boolean petFriendly;
    private int numberOfSeats;

    public VehicleSummaryDto(String vehicleType, int year, String model, String licensePlate, boolean babySeat, boolean petFriendly, int numberOfSeats) {
        this.vehicleType = vehicleType;
        this.year = year;
        this.model = model;
        this.licensePlate = licensePlate;
        this.babySeat = babySeat;
        this.petFriendly = petFriendly;
        this.numberOfSeats = numberOfSeats;
    }

    public String getVehicleType() {
        return this.vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public boolean isBabySeat() {
        return babySeat;
    }

    public void setBabySeat(boolean babySeat) {
        this.babySeat = babySeat;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }
}
