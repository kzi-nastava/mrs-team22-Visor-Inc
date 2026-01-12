package inc.visor.voom_service.shared;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.model.UserType;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.auth.user.repository.UserRoleRepository;
import inc.visor.voom_service.auth.user.repository.UserTypeRepository;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.repository.PersonRepository;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

@Component
@Profile({"dev", "local"})
public class DataInitializer implements ApplicationRunner {

    private final UserTypeRepository userTypeRepository;
    private final UserRoleRepository userRoleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public DataInitializer(UserTypeRepository userTypeRepository, UserRoleRepository userRoleRepository, VehicleTypeRepository vehicleTypeRepository, UserRepository userRepository, PersonRepository personRepository, VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.userTypeRepository = userTypeRepository;
        this.userRoleRepository = userRoleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
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

        if (driverRepository.count() == 0) {
            seedDrivers();
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

    private void seedDrivers() {

        final int NUMBER_OF_DRIVERS = 20;

        UserRole driverRole = userRoleRepository.findByRoleName("DRIVER").orElseThrow();
        UserType driverType = userTypeRepository.findByTypeName("DRIVER").orElseThrow();

        VehicleType standard = vehicleTypeRepository.findByType("STANDARD").orElseThrow();
        VehicleType van = vehicleTypeRepository.findByType("VAN").orElseThrow();
        VehicleType luxury = vehicleTypeRepository.findByType("LUXURY").orElseThrow();

        VehicleType[] vehicleTypes = new VehicleType[]{standard, van, luxury};

        for (int i = 1; i <= NUMBER_OF_DRIVERS; i++) {

            Person person = new Person();
            person.setFirstName("Driver" + i);
            person.setLastName("Lastname" + i);
            person.setAddress("Novi Sad, Street " + i);
            person.setPhoneNumber("+38160123456" + i);
            personRepository.save(person);

            User user = new User();
            user.setEmail("driver" + i + "@gmail.com");
            user.setPassword("password" + i);
            user.setUserRole(driverRole);
            user.setUserType(driverType);
            user.setUserStatus(UserStatus.ACTIVE);
            user.setPerson(person);
            userRepository.save(user);

            Driver driver = new Driver();
            driver.setUser(user);
            driver.setPerson(person);
            driverRepository.save(driver);

            Vehicle vehicle = new Vehicle();
            vehicle.setDriver(driver);
            vehicle.setModel(getVehicleModel(i));
            vehicle.setLicensePlate("NS-" + (100 + i) + "-AB");
            vehicle.setYear(2015 + (i % 8));
            vehicle.setBabySeat(i % 2 == 0);
            vehicle.setPetFriendly(i % 3 == 0);
            vehicle.setNumberOfSeats(4 + (i % 3));
            vehicle.setVehicleType(vehicleTypes[i % vehicleTypes.length]);

            vehicleRepository.save(vehicle);
        }
    }

    private String getVehicleModel(int i) {
        return switch (i % 5) {
            case 0 ->
                "Toyota Corolla";
            case 1 ->
                "Volkswagen Passat";
            case 2 ->
                "Skoda Octavia";
            case 3 ->
                "BMW 320d";
            default ->
                "Mercedes C200";
        };
    }

}
