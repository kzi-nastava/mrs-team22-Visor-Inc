package inc.visor.voom.app.user.home.dto;

import java.util.List;

public class RideRequestDto {

    public Route route;
    public Schedule schedule;
    public Integer vehicleTypeId;
    public Preferences preferences;
    public List<String> linkedPassengers;

    public static class Route {
        public List<Point> points;
    }

    public static class Point {
        public double lat;
        public double lng;
        public int orderIndex;
        public String type;
        public String address;
    }

    public static class Schedule {
        public String type;
        public String startAt;
    }

    public static class Preferences {
        public boolean pets;
        public boolean baby;
    }
}
