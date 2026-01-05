package inc.visor.voom_service.vehicle.model;

import java.util.Objects;

import inc.visor.voom_service.driver.model.Driver;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id", nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "licensePlate", nullable = false, unique = true)
    private String licensePlate;

    @Column(name = "baby_seat", nullable = false)
    private boolean babySeat;

    @Column(name = "pet_friendly", nullable = false)
    private boolean petFriendly;

    @Column(name = "number_of_seats", nullable = false)
    private int numberOfSeats;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
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

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", driver=" + driver +
                ", vehicleType=" + vehicleType +
                ", year=" + year +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", babySeat=" + babySeat +
                ", petFriendly=" + petFriendly +
                ", numberOfSeats=" + numberOfSeats +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id && year == vehicle.year && babySeat == vehicle.babySeat && petFriendly == vehicle.petFriendly && Objects.equals(driver, vehicle.driver) && Objects.equals(vehicleType, vehicle.vehicleType) && numberOfSeats == vehicle.numberOfSeats && Objects.equals(model, vehicle.model) && Objects.equals(licensePlate, vehicle.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driver, vehicleType, year, model, licensePlate, babySeat, petFriendly, numberOfSeats);
    }
}
