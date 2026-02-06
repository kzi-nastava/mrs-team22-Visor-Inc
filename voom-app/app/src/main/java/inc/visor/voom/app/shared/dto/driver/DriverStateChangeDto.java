package inc.visor.voom.app.shared.dto.driver;

public class DriverStateChangeDto {
    private long userId;
    private String currentState;
    private String performedAt;

    public DriverStateChangeDto() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(String performedAt) {
        this.performedAt = performedAt;
    }
}
