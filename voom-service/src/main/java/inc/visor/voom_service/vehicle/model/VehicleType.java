package inc.visor.voom_service.vehicle.model;

import java.util.Objects;

import inc.visor.voom_service.vehicle.dto.CreateVehicleTypeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicle_type")
@Getter
@Setter
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_type_id", nullable = false)
    private long id;

    @Column(name = "vehicle_type_name", nullable = false, unique = true)
    private String type;

    @Column(name = "vehicle_type_price", nullable = false, unique = false)
    private Double price;

    public VehicleType(String type, Double price) {
        this.type = type;
        this.price = price;
    }

    public VehicleType() {
    }

    public VehicleType(CreateVehicleTypeDto dto) {
        this.type = dto.getType();
        this.price = dto.getPrice();
    }

    @Override
    public String toString() {
        return "VehicleType{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VehicleType that = (VehicleType) o;
        return id == that.id && Objects.equals(type, that.type) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, price);
    }
}
