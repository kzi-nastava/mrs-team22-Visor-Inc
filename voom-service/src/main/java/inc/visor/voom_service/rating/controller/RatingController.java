package inc.visor.voom_service.rating.controller;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.rating.dto.RatingRequestDto;
import inc.visor.voom_service.rating.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/rating/{rideId}")
    public ResponseEntity<Void> rateRide(
            @PathVariable Long rideId,
            @RequestBody RatingRequestDto request
    ) {
        ratingService.rateRide(rideId, request);
        System.out.println(rideId);
        return ResponseEntity.noContent().build();
    }
}
