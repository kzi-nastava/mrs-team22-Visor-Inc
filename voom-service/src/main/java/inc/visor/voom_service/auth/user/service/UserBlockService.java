package inc.visor.voom_service.auth.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inc.visor.voom_service.auth.user.dto.BlockUserRequestDto;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserBlockNote;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.repository.UserBlockNoteRepository;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final UserRepository userRepository;
    private final UserBlockNoteRepository blockNoteRepository;

    @Transactional
    public User blockUser(Long userId, Long adminId, BlockUserRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserStatus() == UserStatus.SUSPENDED) {
            throw new RuntimeException("User already blocked");
        }

        Optional<UserBlockNote> existingActiveNote =
                blockNoteRepository.findByUserIdAndActiveTrue(userId);

        existingActiveNote.ifPresent(note -> {
            note.setActive(false);
            blockNoteRepository.save(note);
        });

        user.setUserStatus(UserStatus.SUSPENDED);
        userRepository.save(user);

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        UserBlockNote note = new UserBlockNote();
        note.setUser(user);
        note.setAdmin(admin);
        note.setReason(dto.getReason());
        note.setActive(true);

        blockNoteRepository.save(note);

        return user;
    }

    @Transactional
    public User unblockUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserStatus() != UserStatus.SUSPENDED) {
            throw new RuntimeException("User is not blocked");
        }

        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Optional<UserBlockNote> activeNote =
                blockNoteRepository.findByUserIdAndActiveTrue(userId);

        activeNote.ifPresent(note -> {
            note.setActive(false);
            blockNoteRepository.save(note);
        });

        return user;
    }

    public Optional<UserBlockNote> getActiveBlockNote(Long userId) {
        return blockNoteRepository.findByUserIdAndActiveTrue(userId);
    }
}
