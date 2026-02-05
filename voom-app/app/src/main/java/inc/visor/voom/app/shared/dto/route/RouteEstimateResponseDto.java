package inc.visor.voom.app.shared.dto.route;

public class RouteEstimateResponseDto {
    private int duration;
    private double distance;

    public RouteEstimateResponseDto(int duration, double distance) {
        this.duration = duration;
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
