package inc.visor.voom_service.shared;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import inc.visor.voom_service.auth.user.model.Permission;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.repository.PermissionRepository;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.auth.user.repository.UserRoleRepository;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverState;
import inc.visor.voom_service.driver.model.DriverStateChange;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverActivityRepository;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.repository.PersonRepository;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

@Order(1)
@Component
@Profile({"dev", "local"})
public class DataInitializer implements ApplicationRunner {

    private final UserRoleRepository userRoleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    private final DriverActivityRepository driverActivityRepository;

    public DataInitializer(UserRoleRepository userRoleRepository, VehicleTypeRepository vehicleTypeRepository, UserRepository userRepository, PersonRepository personRepository, VehicleRepository vehicleRepository, DriverRepository driverRepository, PasswordEncoder passwordEncoder, PermissionRepository permissionRepository, DriverActivityRepository driverActivityRepository) {
        this.userRoleRepository = userRoleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.driverActivityRepository = driverActivityRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        Permission userPermission = permissionRepository.readPermissionByPermissionName("USER").orElse(null);
        Permission driverPermission = permissionRepository.readPermissionByPermissionName("DRIVER").orElse(null);
        Permission adminPermission = permissionRepository.readPermissionByPermissionName("ADMIN").orElse(null);

        if (userPermission == null) {
            userPermission = permissionRepository.save(new Permission("USER"));
        }

        if (driverPermission == null) {
            driverPermission = permissionRepository.save(new Permission("DRIVER"));
        }

        if (adminPermission == null) {
            adminPermission = permissionRepository.save(new Permission("ADMIN"));
        }

        if (userRoleRepository.count() == 0) {
            createUserRole("ADMIN", adminPermission);
            createUserRole("DRIVER", driverPermission);
            createUserRole("USER", userPermission);
        }

        if (vehicleTypeRepository.count() == 0) {
            createVehicleType("STANDARD");
            createVehicleType("VAN");
            createVehicleType("LUXURY");
        }

        if (driverRepository.count() == 0) {
            seedDrivers();
        }

        seedUser();

        UserRole adminRole = userRoleRepository.findByRoleName("ADMIN").orElseThrow();

        Person person = new Person();
        person.setFirstName("Admin");
        person.setLastName("Lastname");
        person.setAddress("Novi Sad, Street");
        person.setPhoneNumber("+38160123456");
        person.setBirthDate(LocalDateTime.of(1980, 1, 1, 0, 0));
        person = personRepository.save(person);

        User user = new User();
        user.setEmail("admin1@gmail.com");
        user.setPassword(passwordEncoder.encode("test1234"));
        user.setUserRole(adminRole);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setPerson(person);
        userRepository.save(user);

    }

    private void createUserRole(String name, Permission permission) {
        UserRole role = new UserRole();
        role.setRoleName(name);
        role.setPermissions(Set.of(permission));
        userRoleRepository.save(role);
    }

    private void createVehicleType(String name) {
        VehicleType type = new VehicleType();
        type.setType(name);
        type.setPrice(10.0);
        type = vehicleTypeRepository.save(type);
    }

    private void seedDrivers() {
        final int NUMBER_OF_DRIVERS = 5;

        UserRole driverRole = userRoleRepository.findByRoleName("DRIVER").orElseThrow();
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
            person.setBirthDate(LocalDateTime.of(1980, 1, 1, 0, 0));
            personRepository.save(person);

            User user = new User();
            user.setEmail("driver" + i + "@gmail.com");
            user.setPassword(passwordEncoder.encode("test1234"));
            user.setUserRole(driverRole);
            user.setUserStatus(UserStatus.ACTIVE);
            user.setPerson(person);
            userRepository.save(user);

            Driver driver = new Driver();
            driver.setUser(user);
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver);

            DriverStateChange initChange = new DriverStateChange();
            initChange.setDriver(driver);
            initChange.setCurrentState(DriverState.ACTIVE);
            initChange.setPerformedAt(LocalDateTime.now().minusSeconds(5)); 
            driverActivityRepository.save(initChange);

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

    private void seedUser() {
        final int NUMBER_OF_USERS = 5;

        UserRole userRole = userRoleRepository.findByRoleName("USER").orElseThrow();

        for (int i = 1; i <= NUMBER_OF_USERS; i++) {

            Person person = new Person();
            person.setFirstName("User" + i);
            person.setLastName("Lastname" + i);
            person.setAddress("Novi Sad, Street " + i);
            person.setPhoneNumber("+38160123456" + i);
            person.setBirthDate(LocalDateTime.of(1980, 1, 1, 0, 0));
            personRepository.save(person);

            User user = new User();
            user.setEmail("user" + i + "@gmail.com");
            user.setPassword(passwordEncoder.encode("test1234"));
            user.setUserRole(userRole);
            user.setUserStatus(UserStatus.ACTIVE);
            user.setPerson(person);
            userRepository.save(user);
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
