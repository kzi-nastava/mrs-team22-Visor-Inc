package inc.visor.voom_service.auth.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inc.visor.voom_service.auth.user.model.UserType;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Integer> {
    Optional<UserType> findByTypeName(String typeName);
}
