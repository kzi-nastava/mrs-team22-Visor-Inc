package inc.visor.voom_service.vehicle.model;

import java.util.Objects;

import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.vehicle.dto.CreateVehicleDto;
import inc.visor.voom_service.vehicle.dto.VehicleDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
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

    @Column(name = "licensePlate", nullable = false)
    private String licensePlate;

    @Column(name = "baby_seat", nullable = false)
    private boolean babySeat;

    @Column(name = "pet_friendly", nullable = false)
    private boolean petFriendly;

    @Column(name = "number_of_seats", nullable = false)
    private int numberOfSeats;

    public Vehicle(CreateVehicleDto dto, VehicleType vehicleType, Driver driver) {
        this.driver = driver;
        this.vehicleType = vehicleType;
        this.year = dto.getYear();
        this.model = dto.getModel();
        this.licensePlate = dto.getLicensePlate();
        this.babySeat = dto.isBabySeat();
        this.petFriendly = dto.isPetFriendly();
        this.numberOfSeats = dto.getNumberOfSeats();
    }

    public Vehicle(VehicleDto dto, VehicleType vehicleType, Driver driver) {
        this.id = dto.getId();
        this.driver = driver;
        this.vehicleType = vehicleType;
        this.year = dto.getYear();
        this.model = dto.getModel();
        this.licensePlate = dto.getLicensePlate();
        this.babySeat = dto.isBabySeat();
        this.petFriendly = dto.isPetFriendly();
        this.numberOfSeats = dto.getNumberOfSeats();
    }

    public Vehicle() {
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
