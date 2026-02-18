package inc.visor.voom_service.auth.user.repository;

import inc.visor.voom_service.auth.user.model.UserBlockNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBlockNoteRepository extends JpaRepository<UserBlockNote, Long> {

    Optional<UserBlockNote> findByUserIdAndActiveTrue(Long userId);

}
