package inc.visor.voom_service.driver.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.activation.model.ActivationToken;
import inc.visor.voom_service.activation.service.ActivationTokenService;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.auth.user.repository.UserRoleRepository;
import inc.visor.voom_service.driver.dto.CreateDriverDto;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.driver.dto.VehicleChangeRequestStatus;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverState;
import inc.visor.voom_service.driver.model.DriverStateChange;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.model.DriverVehicleChangeRequest;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.driver.repository.DriverVehicleChangeRequestRepository;
import inc.visor.voom_service.mail.EmailService;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.repository.PersonRepository;
import inc.visor.voom_service.ride.dto.ActiveRideDto;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.route.service.RideRouteService;
import inc.visor.voom_service.shared.utils.GeoUtil;
import inc.visor.voom_service.shared.utils.Helpers;
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
    private final UserRoleRepository userRoleRepository;
    private final PersonRepository personRepository;
    private final DriverVehicleChangeRequestRepository changeRequestRepository;

    private final RideService rideService;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;
    private final DriverActivityService driverActivityService;

    public DriverService(VehicleRepository vehicleRepository, DriverRepository driverRepository, VehicleTypeRepository vehicleTypeRepository, UserRepository userRepository, UserRoleRepository userRoleRepository, PersonRepository personRepository, PasswordEncoder passwordEncoder, EmailService emailService, ActivationTokenService activationTokenService, RideRouteService routeService, RideService rideService, DriverVehicleChangeRequestRepository changeRequestRepository, DriverActivityService driverActivityService) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.activationTokenService = activationTokenService;
        this.rideService = rideService;
        this.changeRequestRepository = changeRequestRepository;
        this.driverActivityService = driverActivityService;
    }

    public void simulateMove(inc.visor.voom_service.driver.dto.DriverLocationDto dto) {
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

    public Driver create(Driver driver) {
        return driverRepository.save(driver);
    }

    public VehicleSummaryDto getVehicle(Long userId) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findByDriverId(driver.getId())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));

        double activeHours = calculateActiveHoursLast24h(driver.getId());

        VehicleSummaryDto dto = new VehicleSummaryDto(
                vehicle.getVehicleType().getType(),
                vehicle.getYear(),
                vehicle.getModel(),
                vehicle.getLicensePlate(),
                vehicle.isBabySeat(),
                vehicle.isPetFriendly(),
                vehicle.getNumberOfSeats(),
                driver.getId(),
                activeHours
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
                vehicle.getNumberOfSeats(),
                driver.getId()
        );

        return dto;
    }

    public void createVehicleChangeRequest(Long userId, VehicleSummaryDto request) {

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Driver not found"));

        Optional<DriverVehicleChangeRequest> existing
                = changeRequestRepository.findByDriverIdAndStatus(
                        driver.getId(),
                        VehicleChangeRequestStatus.PENDING
                );

        if (existing.isPresent()) {
            throw new IllegalStateException("You already have a pending vehicle update request.");
        }

        VehicleType vehicleType = vehicleTypeRepository
                .findByType(request.getVehicleType())
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle type"));

        DriverVehicleChangeRequest changeRequest = new DriverVehicleChangeRequest();

        changeRequest.setDriver(driver);
        changeRequest.setModel(request.getModel());
        changeRequest.setLicensePlate(request.getLicensePlate());
        changeRequest.setNumberOfSeats(request.getNumberOfSeats());
        changeRequest.setBabySeat(request.isBabySeat());
        changeRequest.setPetFriendly(request.isPetFriendly());
        changeRequest.setVehicleType(vehicleType.getType());

        changeRequest.setStatus(VehicleChangeRequestStatus.PENDING);
        changeRequest.setCreatedAt(LocalDateTime.now());

        changeRequestRepository.save(changeRequest);

        User user = driver.getUser();
        String fullName = user.getPerson().getFirstName() + " " + user.getPerson().getLastName();
        String subject = "Vehicle update request from " + fullName;
        String reviewLink = "http://localhost:4200/admin/vehicle-requests/" + changeRequest.getId();
        String androidLink = "voom://vehicle-requests/" + changeRequest.getId();

        String body = """
        <div style="font-family: Arial, sans-serif;">
            <h2>Vehicle Change Request</h2>

            <p><strong>Driver:</strong> %s</p>
            <p><strong>Model:</strong> %s</p>
            <p><strong>License Plate:</strong> %s</p>
            <p><strong>Seats:</strong> %d</p>
            <p><strong>Baby Seat:</strong> %s</p>
            <p><strong>Pet Friendly:</strong> %s</p>

            <br>

            <p>
                🌐 Open in Web:
                <a href="%s">%s</a>
            </p>

            <p>
                📱 Open in Android App:
                <a href="%s">%s</a>
            </p>
        </div>
        """.formatted(
                fullName,
                request.getModel(),
                request.getLicensePlate(),
                request.getNumberOfSeats(),
                request.isBabySeat() ? "Yes" : "No",
                request.isPetFriendly() ? "Yes" : "No",
                reviewLink,
                reviewLink,
                androidLink,
                androidLink
        );

        emailService.send("admin1@gmail.com", subject, body);
    }

    @Transactional
    public Driver createDriver(CreateDriverDto request) {

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
        person.setBirthDate(LocalDateTime.parse(request.getBirthDate()));
        person.setPhoneNumber(request.getPhoneNumber());
        person.setAddress(request.getAddress());

        person = personRepository.save(person);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPerson(person);
        user.setUserRole(userRole);
        user.setUserStatus(UserStatus.NOTACTIVATED);

        String dummyPassword = "test1234";
        user.setPassword(passwordEncoder.encode(dummyPassword));

        user = userRepository.save(user);

        Driver driver = new Driver();
        driver.setUser(user);
        driver.setStatus(DriverStatus.AVAILABLE);

        driver = driverRepository.save(driver);

        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(driver);
        vehicle.setVehicleType(vehicleType);
        vehicle.setModel(request.getVehicle().getModel());
        vehicle.setLicensePlate(request.getVehicle().getLicensePlate());
        vehicle.setNumberOfSeats(request.getVehicle().getNumberOfSeats());
        vehicle.setBabySeat(request.getVehicle().getBabySeat());
        vehicle.setPetFriendly(request.getVehicle().getPetFriendly());

        vehicleRepository.save(vehicle);

        ActivationToken activationToken
                = activationTokenService.createForUser(user);

        String token = activationToken.getToken();

        String webLink = "http://localhost:4200/voom/activate?token=" + token;
        String mobileLink = "voom://activate?token=" + token;

        String subject = "Activate your Voom Driver Account";

        String body = """
<div style="font-family: Arial, sans-serif; padding: 16px;">
    <h2>Welcome to Voom 🚗</h2>

    <p>Your driver account has been successfully created.</p>

    <p>Please activate your account using one of the options below:</p>

    <div style="margin-top: 20px; margin-bottom: 20px;">
        <p><strong>🌐 Activate via Web:</strong></p>
        <a href="%s"
           style="display:inline-block;
                  padding:10px 16px;
                  background-color:#1976d2;
                  color:white;
                  text-decoration:none;
                  border-radius:6px;">
            Activate on Web
        </a>
    </div>

    <div style="margin-top: 20px;">
        <p><strong>📱 Activate via Mobile App:</strong></p>
        <a href="%s"
           style="display:inline-block;
                  padding:10px 16px;
                  background-color:#2e7d32;
                  color:white;
                  text-decoration:none;
                  border-radius:6px;">
            Open in Voom App
        </a>
    </div>

    <br/>

    <p>If the buttons do not work, you can copy the links below:</p>

    <p>Web: %s</p>
    <p>Mobile: %s</p>
</div>
""".formatted(
                webLink,
                mobileLink,
                webLink,
                mobileLink
        );

        emailService.send(user.getEmail(), subject, body);

        return driver;
    }

    public List<Driver> getDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriver(long driverId) {
        return driverRepository.findById(driverId);
    }

    public Optional<Driver> getDriverFromUser(long userId) {
        return driverRepository.findByUserId(userId);
    }

    public Driver updateDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public void deleteDriver(long driverId) {
        driverRepository.deleteById(driverId);
    }

    public List<DriverSummaryDto> getActiveDrivers() {
        List<Driver> activeDrivers = driverRepository.findAll();

        activeDrivers = activeDrivers.stream()
                .filter(driver -> driver.getUser().getUserStatus() == UserStatus.ACTIVE)
                .toList();

        return activeDrivers.stream().map(DriverSummaryDto::new).toList();
    }

    public Driver findDriverForRideRequest(
            RideRequest rideRequest,
            List<RideRequestCreateDto.DriverLocationDto> snapshot
    ) {

        if (snapshot == null || snapshot.isEmpty()) {
            return null;
        }

        RoutePoint pickup = rideRequest.getRideRoute().getPickupPoint();
        Map<Long, RideRequestCreateDto.DriverLocationDto> locMap = Helpers.snapshotToMap(snapshot);

        List<Driver> candidates = snapshot.stream()
                .map(s -> driverRepository.findById(s.driverId).orElse(null))
                .filter(Objects::nonNull)
                .filter(d -> d.getUser().getUserStatus() == UserStatus.ACTIVE)
                .filter(d -> isDriverCurrentlyActive(d.getId()))
                .filter(d -> vehicleMatches(d, rideRequest))
                .filter(d -> !isDriverOverWorked(d))
                .toList();

        if (candidates.isEmpty()) {
            return null;
        }

        if (rideRequest.getScheduleType() == ScheduleType.NOW) {
            candidates = candidates.stream()
                    .filter(d -> d.getStatus() == DriverStatus.AVAILABLE)
                    .toList();
        }

        List<Driver> freeDrivers = candidates.stream()
                .filter(d -> rideService.isDriverFreeForRide(d, rideRequest))
                .toList();

        if (!freeDrivers.isEmpty()) {
            return nearestDriver(freeDrivers, pickup, locMap);
        }

        List<Driver> finishingSoon = candidates.stream()
                .filter(d -> finishesInNext10Minutes(d))
                .toList();

        if (!finishingSoon.isEmpty()) {
            return nearestDriver(finishingSoon, pickup, locMap);
        }

        return null;
    }

    private boolean finishesInNext10Minutes(Driver driver) {
        return rideService.findActiveRides(driver.getId())
                .stream()
                .anyMatch(r -> Duration.between(
                LocalDateTime.now(),
                rideService.estimateRideEndTime(r)
        ).toMinutes() <= 10);
    }

    private boolean isDriverOverWorked(Driver driver) {
        double activeHours = calculateActiveHoursLast24h(driver.getId());
        return activeHours >= 8;
    }

    private boolean vehicleMatches(Driver driver, RideRequest req) {
        Vehicle vehicle = vehicleRepository.findByDriverId(driver.getId())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));

        if (!vehicle.getVehicleType().equals(req.getVehicleType())) {
            return false;
        }

        if (req.isPetTransport() && !vehicle.isPetFriendly()) {
            return false;
        }

        if (req.isBabyTransport() && !vehicle.isBabySeat()) {
            return false;
        }

        if (vehicle.getNumberOfSeats() < req.getLinkedPassengerEmails().size() + 1) {
            return false;
        }

        return true;
    }

    public ActiveRideDto getActiveRide(Long userId) {
        Ride activeRide = rideService.findActiveRide(userId);

        if (activeRide == null) {
            return null;
        }
        ActiveRideDto dto = new ActiveRideDto();
        dto.setRideId(activeRide.getId());
        dto.setStatus(activeRide.getStatus());
        dto.setRoutePoints(
                activeRide.getRideRequest().getRideRoute().getRoutePoints().stream().map(RoutePoint::toDto).toList()
        );

        return dto;
    }

    private Driver nearestDriver(
            List<Driver> drivers,
            RoutePoint pickup,
            Map<Long, RideRequestCreateDto.DriverLocationDto> locMap
    ) {
        return drivers.stream()
                .min(Comparator.comparingDouble(d -> {
                    RideRequestCreateDto.DriverLocationDto loc = locMap.get(d.getId());
                    return GeoUtil.distanceKm(
                            pickup.getLatitude(),
                            pickup.getLongitude(),
                            loc.lat,
                            loc.lng
                    );
                }))
                .orElse(null);
    }

    public void save(Driver driver) {
        driverRepository.save(driver);
    }

    public double calculateActiveHoursLast24h(Long driverId) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusHours(24);

        List<DriverStateChange> changes
                = driverActivityService.findChangesSince(driverId, since);

        List<DriverStateChange> lastBefore
                = driverActivityService.findLastChangeBefore(driverId, since);

        double activeSeconds = 0;

        if (!lastBefore.isEmpty() && lastBefore.get(0).getCurrentState() == DriverState.ACTIVE) {

            LocalDateTime end
                    = changes.isEmpty() ? now : changes.get(0).getPerformedAt();

            activeSeconds += Duration.between(since, end).getSeconds();
        }

        for (int i = 0; i < changes.size(); i++) {

            DriverStateChange current = changes.get(i);

            if (current.getCurrentState() == DriverState.ACTIVE) {

                LocalDateTime start = current.getPerformedAt();
                LocalDateTime end
                        = (i + 1 < changes.size())
                        ? changes.get(i + 1).getPerformedAt()
                        : now;

                if (end.isBefore(since)) {
                    continue;
                }

                start = start.isBefore(since) ? since : start;
                end = end.isAfter(now) ? now : end;

                activeSeconds += Duration.between(start, end).getSeconds();
            }
        }

        return activeSeconds / 3600.0;
    }

    private boolean isDriverCurrentlyActive(Long driverId) {
        return driverActivityService
                .getLastStateChange(driverId)
                .map(DriverStateChange::getCurrentState)
                .map(state -> state == DriverState.ACTIVE)
                .orElse(false);
    }

}
