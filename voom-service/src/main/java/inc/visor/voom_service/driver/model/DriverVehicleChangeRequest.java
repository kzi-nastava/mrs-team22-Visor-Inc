package inc.visor.voom_service.driver.model;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.dto.VehicleChangeRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "driver_vehicle_change_requests")
@Getter
@Setter
public class DriverVehicleChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(name = "number_of_seats", nullable = false)
    private int numberOfSeats;

    @Column(name = "baby_seat", nullable = false)
    private boolean babySeat;

    @Column(name = "pet_friendly", nullable = false)
    private boolean petFriendly;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleChangeRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // koji admin je odobrio/odbio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DriverVehicleChangeRequest that = (DriverVehicleChangeRequest) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = VehicleChangeRequestStatus.PENDING;
    }

}
