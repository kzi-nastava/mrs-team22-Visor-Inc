package inc.visor.voom_service.chat.service;

import inc.visor.voom_service.auth.dto.UserDto;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatRepository;
    private final UserRepository userRepository;

    public List<UserDto> findAllChatPartnersForAdmin() {
        List<String> partnerEmails = chatRepository.findDistinctChatPartners();

        return partnerEmails.stream()
                .map(userRepository::findByEmail)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
}
