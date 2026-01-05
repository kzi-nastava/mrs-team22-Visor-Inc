package inc.visor.voom_service.driver.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.model.UserType;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.auth.user.repository.UserRoleRepository;
import inc.visor.voom_service.auth.user.repository.UserTypeRepository;
import inc.visor.voom_service.driver.dto.CreateDriverDto;
import inc.visor.voom_service.driver.dto.DriverLocationDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.repository.PersonRepository;
import inc.visor.voom_service.vehicle.dto.VehicleSummaryDto;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;
import jakarta.transaction.Transactional;

@Service
public class DriverService {

    private final PasswordEncoder passwordEncoder;

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final UserRoleRepository userRoleRepository;
    private final PersonRepository personRepository;

    public DriverService(VehicleRepository vehicleRepository, DriverRepository driverRepository, VehicleTypeRepository vehicleTypeRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, UserRoleRepository userRoleRepository, PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.userRoleRepository = userRoleRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void simulateMove(DriverLocationDto dto) {
        // used for simulating movement
        // one possible way is to 1. select 2 random points in Novi Sad
        // 2. call external api to get route and waypoints between these two
        // 3. update position for each waypoint that api returns
        // 4. broadcast update
        return;
    }

    public void reportDriver(Long driverId, Long userId, String comment) {
        return;
    }

    public VehicleSummaryDto getVehicle(Long userId) {

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findByDriverId(driver.getId())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));

        VehicleSummaryDto dto = new VehicleSummaryDto(
                vehicle.getVehicleType().getType(),
                vehicle.getYear(),
                vehicle.getModel(),
                vehicle.getLicensePlate(),
                vehicle.isBabySeat(),
                vehicle.isPetFriendly(),
                vehicle.getNumberOfSeats()
        );

        return dto;
    }

    public VehicleSummaryDto updateVehicle(Long userId, VehicleSummaryDto request) {

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findByDriverId(driver.getId())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));

        VehicleType vehicleType
                = vehicleTypeRepository.findByType(request.getVehicleType())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle type"));

        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setNumberOfSeats(request.getNumberOfSeats());
        vehicle.setBabySeat(request.isBabySeat());
        vehicle.setPetFriendly(request.isPetFriendly());
        vehicle.setVehicleType(vehicleType);

        vehicleRepository.save(vehicle);

        VehicleSummaryDto dto = new VehicleSummaryDto(
                vehicle.getVehicleType().getType(),
                vehicle.getYear(),
                vehicle.getModel(),
                vehicle.getLicensePlate(),
                vehicle.isBabySeat(),
                vehicle.isPetFriendly(),
                vehicle.getNumberOfSeats()
        );

        return dto;
    }

    @Transactional
    public void createDriver(CreateDriverDto request) {

        UserType userType = userTypeRepository
                .findByTypeName("DRIVER")
                .orElseThrow(()
                        -> new IllegalStateException("UserType DRIVER not found")
                );

        UserRole userRole = userRoleRepository
                .findByRoleName("DRIVER")
                .orElseThrow(()
                        -> new IllegalStateException("UserRole DRIVER not found")
                );

        VehicleType vehicleType = vehicleTypeRepository
                .findByType(request.getVehicle().getVehicleType())
                .orElseThrow(()
                        -> new IllegalArgumentException("Invalid vehicle type")
                );

        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setAddress(request.getAddress());

        personRepository.save(person);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPerson(person);
        user.setUserType(userType);
        user.setUserRole(userRole);
        user.setUserStatus(UserStatus.INACTIVE);

        String dummyPassword = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(dummyPassword));

        userRepository.save(user);

        Driver driver = new Driver();
        driver.setUser(user);
        driver.setPerson(person);

        driverRepository.save(driver);

        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(driver);
        vehicle.setVehicleType(vehicleType);
        vehicle.setModel(request.getVehicle().getModel());
        vehicle.setLicensePlate(request.getVehicle().getLicensePlate());
        vehicle.setNumberOfSeats(request.getVehicle().getNumberOfSeats());
        vehicle.setBabySeat(request.getVehicle().getBabySeat());
        vehicle.setPetFriendly(request.getVehicle().getPetFriendly());

        vehicleRepository.save(vehicle);

    }

}
