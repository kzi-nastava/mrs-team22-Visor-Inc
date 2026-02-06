package inc.visor.voom_service.shared.notification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.person.service.UserProfileService;
import inc.visor.voom_service.shared.notification.model.Notification;
import inc.visor.voom_service.shared.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserProfileService userService;

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnread(@AuthenticationPrincipal VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = user.getId();
        return ResponseEntity.ok(
                notificationService.getUnreadForUser(userId)
        );
    }

}
