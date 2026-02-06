package inc.visor.voom_service.auth.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import inc.visor.voom_service.auth.user.model.UserBlockNote;

public interface UserBlockNoteRepository extends JpaRepository<UserBlockNote, Long> {

    Optional<UserBlockNote> findByUserIdAndActiveTrue(Long userId);

}
