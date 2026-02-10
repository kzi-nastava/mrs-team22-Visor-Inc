package inc.visor.voom_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
          .getFieldErrors()
          .forEach(error ->
              errors.put(error.getField(), error.getDefaultMessage())
          );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(NotFoundException ex) {
        final ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DriverNotAvailableException.class)
    public ResponseEntity<String> handleDriverUnavailable(DriverNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("No active drivers available");
    }

    @ExceptionHandler(RideScheduleTooLateException.class)
    public ResponseEntity<String> handleSchedule(RideScheduleTooLateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ride can be scheduled max 5 hours ahead");
    }

    @ExceptionHandler(DriverOverworkedException.class)
    public ResponseEntity<String> handleDriverHours(DriverOverworkedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Driver exceeded allowed working hours");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected server error");
    }
}
