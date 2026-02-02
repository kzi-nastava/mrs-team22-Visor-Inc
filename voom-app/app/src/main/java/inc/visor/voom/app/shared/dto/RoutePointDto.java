package inc.visor.voom.app.shared.dto;

import com.google.gson.annotations.SerializedName;

public class RoutePointDto {
    @SerializedName("order")
    public Integer orderIndex;

    public Double lat;

    public Double lng;

    public String address;

    public RoutePointType type;

    public RoutePointDto() {}

    public String toString() {
        return String.valueOf(this.lat) + " " + String.valueOf(this.lng) + String.valueOf(this.orderIndex);

    }

}
