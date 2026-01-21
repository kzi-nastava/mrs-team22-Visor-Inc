package inc.visor.voom_service.ride.dto;

public class StartScheduleRideDto {

    private Long driverId;
    private Double lat;
    private Double lng;

    public StartScheduleRideDto() {  }

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
