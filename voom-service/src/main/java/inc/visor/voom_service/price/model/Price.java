package inc.visor.voom_service.price.model;

import inc.visor.voom_service.vehicle.model.VehicleType;
import jakarta.persistence.*;

@Entity
@Table(name = "price")
public class Price {

    @Id
    @Column(name = "price")
    private Long priceId;

    @OneToOne
    @JoinColumn(name = "vehicle_type_id", referencedColumnName = "vehicle_type_id")
    private VehicleType vehicleType;

    @Column(name = "price_per_km", nullable = false)
    private Double pricePerKm;

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long pricingId) {
        this.priceId = pricingId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(Double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }
}
