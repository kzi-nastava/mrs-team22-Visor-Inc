package inc.visor.voom_service.exception;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class ErrorResponse {
    private final String message;
    private final String statusCode;
    private final Long timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.statusCode = null;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ErrorResponse(String message, String statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = Instant.now().toEpochMilli();
    }


}
