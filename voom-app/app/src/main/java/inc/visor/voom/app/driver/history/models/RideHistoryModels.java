package inc.visor.voom.app.driver.history.models;
import java.time.LocalDateTime;
import java.util.List;
public class RideHistoryModels {

    public static class RideHistoryDto {
        public Long id;
        public RideStatus status;
        public RideRequest rideRequest;
        public RideRoute rideRoute;
        public String startedAt;
        public String finishedAt;
        public List<User> passengers;
        public User cancelledBy;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RideStatus getStatus() {
            return status;
        }

        public void setStatus(RideStatus status) {
            this.status = status;
        }

        public RideRequest getRideRequest() {
            return rideRequest;
        }

        public void setRideRequest(RideRequest rideRequest) {
            this.rideRequest = rideRequest;
        }

        public RideRoute getRideRoute() {
            return rideRoute;
        }

        public void setRideRoute(RideRoute rideRoute) {
            this.rideRoute = rideRoute;
        }

        public String getStartedAt() {
            return startedAt;
        }

        public void setStartedAt(String startedAt) {
            this.startedAt = startedAt;
        }

        public String getFinishedAt() {
            return finishedAt;
        }

        public void setFinishedAt(String finishedAt) {
            this.finishedAt = finishedAt;
        }

        public List<User> getPassengers() {
            return passengers;
        }

        public void setPassengers(List<User> passengers) {
            this.passengers = passengers;
        }

        public User getCancelledBy() {
            return cancelledBy;
        }

        public void setCancelledBy(User cancelledBy) {
            this.cancelledBy = cancelledBy;
        }
    }

    public static class RideRequest {
        public Long id;
        public User creator;
        public RideRoute rideRoute;
        public RideRequestStatus status;
        public ScheduleType scheduleType;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public User getCreator() {
            return creator;
        }

        public void setCreator(User creator) {
            this.creator = creator;
        }

        public RideRoute getRideRoute() {
            return rideRoute;
        }

        public void setRideRoute(RideRoute rideRoute) {
            this.rideRoute = rideRoute;
        }

        public RideRequestStatus getStatus() {
            return status;
        }

        public void setStatus(RideRequestStatus status) {
            this.status = status;
        }

        public ScheduleType getScheduleType() {
            return scheduleType;
        }

        public void setScheduleType(ScheduleType scheduleType) {
            this.scheduleType = scheduleType;
        }

        public String getScheduledTime() {
            return scheduledTime;
        }

        public void setScheduledTime(String scheduledTime) {
            this.scheduledTime = scheduledTime;
        }

        public VehicleType getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
        }

        public boolean isBabyTransport() {
            return babyTransport;
        }

        public void setBabyTransport(boolean babyTransport) {
            this.babyTransport = babyTransport;
        }

        public boolean isPetTransport() {
            return petTransport;
        }

        public void setPetTransport(boolean petTransport) {
            this.petTransport = petTransport;
        }

        public double getCalculatedPrice() {
            return calculatedPrice;
        }

        public void setCalculatedPrice(double calculatedPrice) {
            this.calculatedPrice = calculatedPrice;
        }

        public List<String> getLinkedPassengerEmails() {
            return linkedPassengerEmails;
        }

        public void setLinkedPassengerEmails(List<String> linkedPassengerEmails) {
            this.linkedPassengerEmails = linkedPassengerEmails;
        }

        public User getCancelledBy() {
            return cancelledBy;
        }

        public void setCancelledBy(User cancelledBy) {
            this.cancelledBy = cancelledBy;
        }

        public String scheduledTime;
        public VehicleType vehicleType;
        public boolean babyTransport;
        public boolean petTransport;
        public double calculatedPrice;
        public List<String> linkedPassengerEmails;
        public User cancelledBy;
    }

    public static class User {
        public Long id;
        public String email;
        public Person person;
        public UserStatus userStatus;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public UserStatus getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(UserStatus userStatus) {
            this.userStatus = userStatus;
        }

        public UserRole getUserRole() {
            return userRole;
        }

        public void setUserRole(UserRole userRole) {
            this.userRole = userRole;
        }

        public UserRole userRole;
    }

    public static class Person {
        public Long id;
        public String firstName;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String lastName;
        public String phoneNumber;
        public String address;
        public String profilePicture;
    }

    public enum UserStatus {
        INACTIVE,
        ACTIVE,
        SUSPENDED,
        PENDING,
        NOTACTIVATED
    }

    public static class UserRole {
        public Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String name;
    }

    public static class RideRoute {
        public Long id;
        public double totalDistanceKm;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public double getTotalDistanceKm() {
            return totalDistanceKm;
        }

        public void setTotalDistanceKm(double totalDistanceKm) {
            this.totalDistanceKm = totalDistanceKm;
        }

        public List<RoutePoint> getRoutePoints() {
            return routePoints;
        }

        public void setRoutePoints(List<RoutePoint> routePoints) {
            this.routePoints = routePoints;
        }

        public List<RoutePoint> routePoints;

        public RoutePoint getPickup() {
            return this.routePoints.stream()
                    .filter(rp -> rp.getPointType() == RoutePointType.PICKUP)
                    .findFirst()
                    .orElse(null);
        }

        public RoutePoint getDropOff() {
            return this.routePoints.stream()
                    .filter(rp -> rp.getPointType() == RoutePointType.DROPOFF)
                    .findFirst()
                    .orElse(null);
        }
    }

    public static class RoutePoint {
        public int orderIndex;
        public double lat;

        public int getOrderIndex() {
            return orderIndex;
        }

        public void setOrderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public RoutePointType getPointType() {
            return pointType;
        }

        public void setPointType(RoutePointType pointType) {
            this.pointType = pointType;
        }

        public double lng;
        public String address;
        public RoutePointType pointType;
    }

    public static class VehicleType {
        public Long id;
        public String type;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getPricePerKm() {
            return pricePerKm;
        }

        public void setPricePerKm(double pricePerKm) {
            this.pricePerKm = pricePerKm;
        }

        public double pricePerKm;
    }

    public static enum RideStatus {
        SCHEDULED,
        ONGOING,
        STARTED,
        DRIVER_CANCELLED,
        FINISHED,
        PANIC,
        STOPPED,
        USER_CANCELLED
    }

    public static enum RideRequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
    }

    public static enum ScheduleType {
        NOW,
        LATER
    }

    public static enum RoutePointType {
        PICKUP,
        STOP,
        DROPOFF,
        STOPPED
    }

}
