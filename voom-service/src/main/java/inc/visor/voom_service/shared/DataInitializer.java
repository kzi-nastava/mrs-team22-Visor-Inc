package inc.visor.voom_service.shared;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserType;
import inc.visor.voom_service.auth.user.repository.UserRoleRepository;
import inc.visor.voom_service.auth.user.repository.UserTypeRepository;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

@Component
@Profile({"dev", "local"})
public class DataInitializer implements ApplicationRunner {

    private final UserTypeRepository userTypeRepository;
    private final UserRoleRepository userRoleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    public DataInitializer(UserTypeRepository userTypeRepository, UserRoleRepository userRoleRepository, VehicleTypeRepository vehicleTypeRepository) {
        this.userTypeRepository = userTypeRepository;
        this.userRoleRepository = userRoleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (userTypeRepository.count() == 0) {
            createUserType("ADMIN");
            createUserType("DRIVER");
            createUserType("PASSENGER");
        }

        if (userRoleRepository.count() == 0) {
            createUserRole("ADMIN");
            createUserRole("DRIVER");
            createUserRole("PASSENGER");
        }

        if (vehicleTypeRepository.count() == 0) {
            createVehicleType("STANDARD");
            createVehicleType("VAN");
            createVehicleType("LUXURY");
        }
    }

    private void createUserType(String name) {
        UserType type = new UserType();
        type.setTypeName(name);
        userTypeRepository.save(type);
    }

    private void createUserRole(String name) {
        UserRole role
                = new UserRole();
        role.setRoleName(name);
        userRoleRepository.save(role);
    }

    private void createVehicleType(String name) {
        VehicleType type
                = new VehicleType();
        type.setType(name);
        vehicleTypeRepository.save(type);
    }
}
