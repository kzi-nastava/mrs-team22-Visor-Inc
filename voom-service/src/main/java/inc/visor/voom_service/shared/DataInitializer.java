package inc.visor.voom_service.shared;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
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
import inc.visor.voom_service.driver.model.enums.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.repository.PersonRepository;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.repository.RideRequestRepository;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

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
    private final RideRequestRepository rideRequestRepository;
    private final RideRepository rideRepository;
    private final PermissionRepository permissionRepository;

    public DataInitializer(UserRoleRepository userRoleRepository, VehicleTypeRepository vehicleTypeRepository, UserRepository userRepository, PersonRepository personRepository, VehicleRepository vehicleRepository, DriverRepository driverRepository, PasswordEncoder passwordEncoder, RideRequestRepository rideRequestRepository, RideRepository rideRepository, PermissionRepository permissionRepository) {
        this.userRoleRepository = userRoleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
        this.rideRequestRepository = rideRequestRepository;
        this.rideRepository = rideRepository;
        this.permissionRepository = permissionRepository;
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

         // generateRide();

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
        vehicleTypeRepository.save(type);
    }

    private void seedDrivers() {

        final int NUMBER_OF_DRIVERS = 20;

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

    private void generateRide() {

//        enum ScheduleType {
//            NOW,
//            LATER
//        }

        // ===== PERSONS =====
        Person creatorPerson = new Person(
                "Marko",
                "Marković",
                "061111111",
                "Bulevar Oslobođenja 45, Novi Sad"
        );

        Person passengerPerson = new Person(
                "Petar",
                "Petrović",
                "062222222",
                "Zmaj Jovina 12, Novi Sad"
        );

        personRepository.saveAll(List.of(creatorPerson, passengerPerson));

        // ===== ROLES (ASSUMING THEY EXIST) =====
        UserRole userRole = userRoleRepository.findByRoleName("USER")
                .orElseThrow();

        // ===== USERS =====
        User creator = new User(
                "marko@test.com",
                "password123",              // dummy
                UserStatus.ACTIVE,
                userRole,
                creatorPerson
        );

        User passenger = new User(
                "petar@test.com",
                "password321",
                UserStatus.ACTIVE,
                userRole,
                passengerPerson
        );

        userRepository.saveAll(List.of(creator, passenger));

        // ===== VEHICLE TYPE =====
        VehicleType vehicleType = vehicleTypeRepository.findByType("STANDARD")
                .orElseThrow(); // assume exists

        // ===== ROUTE POINTS =====
        RoutePoint pickup = new RoutePoint();
        pickup.setOrderIndex(0);
        pickup.setPointType(RoutePointType.PICKUP);
        pickup.setAddress("Bulevar Oslobođenja 45, Novi Sad");

        RoutePoint dropoff = new RoutePoint();
        dropoff.setOrderIndex(1);
        dropoff.setPointType(RoutePointType.DROPOFF);
        dropoff.setAddress("Zmaj Jovina 12, Novi Sad");

        // ===== RIDE ROUTE =====
        RideRoute route = new RideRoute();
        route.setTotalDistanceKm(5.4);
        route.setRoutePoints(List.of(pickup, dropoff));

        // ===== RIDE REQUEST =====
        RideRequest rideRequest = new RideRequest();
        rideRequest.setCreator(creator);
        rideRequest.setRideRoute(route);
        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        rideRequest.setScheduleType(ScheduleType.NOW);
        rideRequest.setVehicleType(vehicleType);
        rideRequest.setCalculatedPrice(850);
        rideRequest.setBabyTransport(false);
        rideRequest.setPetTransport(false);
        rideRequest.setLinkedPassengerEmails(List.of(passenger.getEmail()));

        rideRequestRepository.save(rideRequest);

        // ===== EXISTING DRIVER (ID = 1) =====
        Driver driver = driverRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Driver 1 does not exist"));

        // ===== RIDE =====
        Ride ride = new Ride();
        ride.setRideRequest(rideRequest);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ONGOING);
        ride.setStartedAt(LocalDateTime.now());
        ride.setPassengers(List.of(creator, passenger));

        rideRepository.save(ride);
    }


}
