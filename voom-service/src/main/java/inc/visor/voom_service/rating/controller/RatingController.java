package inc.visor.voom_service.rating.controller;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.rating.dto.RatingRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
public class RatingController {

    @PostMapping("/ride/{rideId}")
    public ResponseEntity<Void> rateRide(@PathVariable Long rideId, @Valid @RequestBody RatingRequestDto request, @AuthenticationPrincipal User user) {

        // if date.today() - ride.getFinishedAt() > 3 days -> return error code

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
