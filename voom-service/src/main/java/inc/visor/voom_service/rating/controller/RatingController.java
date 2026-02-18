package inc.visor.voom_service.rating.controller;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.person.service.UserProfileService;
import inc.visor.voom_service.rating.dto.RatingRequestDto;
import inc.visor.voom_service.rating.dto.RatingSummaryDto;
import inc.visor.voom_service.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;
    private final UserProfileService userProfileService;

    @PostMapping("/{rideId}")
    public ResponseEntity<Void> rateRide(@AuthenticationPrincipal VoomUserDetails userDetails, @PathVariable Long rideId, @Valid @RequestBody RatingRequestDto request) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ratingService.rateRide(user, rideId, request);
        System.out.println(rideId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<RatingSummaryDto>> getRatingsByRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(ratingService.getByRide(rideId)
                .stream()
                .map(RatingSummaryDto::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RatingSummaryDto>> getRatingsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(ratingService.getByDriver(driverId).stream().map(RatingSummaryDto::new).collect(Collectors.toList()));
    }
}
