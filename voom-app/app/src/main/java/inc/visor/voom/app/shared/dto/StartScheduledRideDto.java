package inc.visor.voom.app.shared.dto;


public class StartScheduledRideDto {

    private Long driverId;
    private Double lat;
    private Double lng;

    public StartScheduledRideDto() {  }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}
