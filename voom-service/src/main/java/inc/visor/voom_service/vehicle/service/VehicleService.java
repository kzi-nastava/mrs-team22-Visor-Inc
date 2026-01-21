package inc.visor.voom_service.vehicle.service;

import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getVehicles() {
        return this.vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicle(Long id) {
        return this.vehicleRepository.findById(id);
    }

    public Vehicle create(Vehicle vehicle) {
        return this.vehicleRepository.save(vehicle);
    }

    public Vehicle update(Vehicle vehicle) {
        return this.vehicleRepository.save(vehicle);
    }

}
