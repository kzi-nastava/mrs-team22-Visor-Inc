package inc.visor.voom.app.shared.model;

public class DriverLocationDto {

    private long driverId;
    private double lat;
    private double lng;

    public DriverLocationDto(long driverId, double lat, double lng) {
        this.driverId = driverId;
        this.lat = lat;
        this.lng = lng;
    }

    public DriverLocationDto() {
    }

    public long getDriverId() {
        return driverId;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

