package inc.visor.voom_service.vehicle.service;

import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;

    public VehicleTypeService(VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public List<VehicleType> getVehicles() {
        return this.vehicleTypeRepository.findAll();
    }

    public Optional<VehicleType> getVehicle(Long id) {
        return this.vehicleTypeRepository.findById(id);
    }

    public VehicleType create(VehicleType vehicleType) {
        return this.vehicleTypeRepository.save(vehicleType);
    }

    public VehicleType update(VehicleType vehicleType) {
        return this.vehicleTypeRepository.save(vehicleType);
    }

}
