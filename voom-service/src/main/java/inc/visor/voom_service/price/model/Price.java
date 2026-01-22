package inc.visor.voom_service.price.model;

import inc.visor.voom_service.price.dto.CreatePriceDto;
import inc.visor.voom_service.price.dto.PriceDto;
import inc.visor.voom_service.vehicle.model.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "price")
@Getter
@Setter
public class Price {

    @Id
    @Column(name = "price")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @OneToOne
    @JoinColumn(name = "vehicle_type_id", referencedColumnName = "vehicle_type_id")
    private VehicleType vehicleType;

    @Column(name = "price_per_km", nullable = false)
    private Double pricePerKm;

    public Price() {
    }

    public Price(Double pricePerKm, VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        this.pricePerKm = pricePerKm;
    }

    public Price(PriceDto dto, VehicleType vehicleType) {
        this.priceId = dto.getPriceId();
        this.vehicleType = vehicleType;
        this.pricePerKm = dto.getPricePerKm();
    }

    @Override
    public String toString() {
        return "Price{" +
                "priceId=" + priceId +
                ", vehicleType=" + vehicleType +
                ", pricePerKm=" + pricePerKm +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(priceId, price.priceId) && Objects.equals(vehicleType, price.vehicleType) && Objects.equals(pricePerKm, price.pricePerKm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priceId, vehicleType, pricePerKm);
    }
}
