package inc.visor.voom_service.auth.user.service;

import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public UserRole create(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    public Optional<UserRole> read(String roleName) {
        return userRoleRepository.findByRoleName(roleName);
    }


}
