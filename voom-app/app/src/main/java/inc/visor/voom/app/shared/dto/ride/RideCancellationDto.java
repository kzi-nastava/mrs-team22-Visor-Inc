package inc.visor.voom.app.shared.dto.ride;

public class RideCancellationDto {
    private long userId;
    private String message;

    public RideCancellationDto() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
