package inc.visor.voom_service.ride.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateFavoriteRouteException extends RuntimeException {

    public DuplicateFavoriteRouteException() {
        super("Favorite route with same points already exists");
    }
}
