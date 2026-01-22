package inc.visor.voom_service.auth.user.controller;

import inc.visor.voom_service.auth.user.dto.UserRoleDto;
import inc.visor.voom_service.auth.user.service.UserRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public ResponseEntity<List<UserRoleDto>> getUserRoles() {
        return ResponseEntity.ok(this.userRoleService.getUserRoles().stream().map(UserRoleDto::new).toList());
    }

}
