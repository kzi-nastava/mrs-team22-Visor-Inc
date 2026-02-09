package inc.visor.voom.app.user.home.dto;

import java.util.List;

import inc.visor.voom.app.shared.model.DriverLocationDto;

public class RideRequestDto {

    public Route route;
    public Schedule schedule;
    public Integer vehicleTypeId;
    public Preferences preferences;
    public List<String> linkedPassengers;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Integer getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Integer vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public List<String> getLinkedPassengers() {
        return linkedPassengers;
    }

    public void setLinkedPassengers(List<String> linkedPassengers) {
        this.linkedPassengers = linkedPassengers;
    }

    public List<DriverLocationDto> getFreeDriversSnapshot() {
        return freeDriversSnapshot;
    }

    public void setFreeDriversSnapshot(List<DriverLocationDto> freeDriversSnapshot) {
        this.freeDriversSnapshot = freeDriversSnapshot;
    }

    public static class Route {
        public List<Point> points;

        public List<Point> getPoints() {
            return points;
        }

        public void setPoints(List<Point> points) {
            this.points = points;
        }
    }

    public List<DriverLocationDto> freeDriversSnapshot;

    public static class Point {
        public double lat;
        public double lng;
        public int orderIndex;
        public String type;
        public String address;

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

        public int getOrderIndex() {
            return orderIndex;
        }

        public void setOrderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class Schedule {
        public String type;
        public String startAt;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStartAt() {
            return startAt;
        }

        public void setStartAt(String startAt) {
            this.startAt = startAt;
        }
    }

    public static class Preferences {
        public boolean pets;
        public boolean baby;

        public boolean isPets() {
            return pets;
        }

        public void setPets(boolean pets) {
            this.pets = pets;
        }

        public boolean isBaby() {
            return baby;
        }

        public void setBaby(boolean baby) {
            this.baby = baby;
        }
    }

}
