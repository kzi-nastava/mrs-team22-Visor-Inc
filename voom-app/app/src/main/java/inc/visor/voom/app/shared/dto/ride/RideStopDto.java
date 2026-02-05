package inc.visor.voom.app.shared.dto.ride;

import java.time.LocalDateTime;

public class RideStopDto {

    private Long userId;
    private LatLng point;
    private LocalDateTime timestamp;

    public RideStopDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LatLng getPoint() {
        return point;
    }

    public void setPoint(LatLng point) {
        this.point = point;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
