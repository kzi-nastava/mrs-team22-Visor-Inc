package inc.visor.voom_service.complaints.controller;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.complaints.dto.ComplaintRequestDto;
import inc.visor.voom_service.complaints.dto.ComplaintSummaryDto;
import inc.visor.voom_service.complaints.service.ComplaintService;
import inc.visor.voom_service.person.service.UserProfileService;
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
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserProfileService userProfileService;

    @PostMapping("/ride/{rideId}")
    public ResponseEntity<Void> reportRide(@AuthenticationPrincipal VoomUserDetails userDetails, @PathVariable Long rideId, @RequestBody @Valid ComplaintRequestDto body) {
        final String username = userDetails != null ? userDetails.getUsername() : null;
        final User user = userProfileService.getUserByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        complaintService.reportRide(rideId, user, body.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<ComplaintSummaryDto>> getComplaintsByRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(complaintService.getByRide(rideId).stream().map(ComplaintSummaryDto::new).collect(Collectors.toList()));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<ComplaintSummaryDto>> getComplaintsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(complaintService.getByDriver(driverId).stream().map(ComplaintSummaryDto::new).collect(Collectors.toList()));
    }
}
