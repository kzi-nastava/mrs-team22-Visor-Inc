package inc.visor.voom_service.vehicle.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicle_type")
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_type_id", nullable = false)
    private long id;

    @Column(name = "vehicle_type_name", nullable = false, unique = true)
    private String type;

    public VehicleType(String type) {
        this.type = type;
    }

    public VehicleType() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBasePrice() {
        return switch (type) {
            case "CAR" -> 5.0;
            case "VAN" -> 8.0;
            case "LUXURY" -> 15.0;
            default -> 0.0;
        };
    }

    @Override
    public String toString() {
        return "VehicleTypeController{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VehicleType that = (VehicleType) o;
        return id == that.id && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
