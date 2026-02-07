package inc.visor.voom.app.shared.dto;

import com.google.gson.annotations.SerializedName;

public class NotificationDto {

    @SerializedName("id")
    public Long id;

    @SerializedName("type")
    public String type;

    @SerializedName("title")
    public String title;

    @SerializedName("message")
    public String message;

    @SerializedName("rideId")
    public Long rideId;

    @SerializedName("read")
    public boolean read;

    @SerializedName("createdAt")
    public String createdAt;

}
