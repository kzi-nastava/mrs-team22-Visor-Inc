package inc.visor.voom_service.auth.user.repository;

import inc.visor.voom_service.auth.user.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> readPermissionByPermissionName(String permissionName);
}
