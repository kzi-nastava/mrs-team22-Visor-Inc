package inc.visor.voom_service.person.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.person.dto.ChangePasswordRequestDto;
import inc.visor.voom_service.person.dto.UpdateUserProfileRequestDto;
import inc.visor.voom_service.person.dto.UserProfileResponseDto;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.repository.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class UserProfileService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileService(PersonRepository personRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponseDto getProfile(User user) {
        Person person = user.getPerson();

        UserProfileResponseDto dto = new UserProfileResponseDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setPhoneNumber(person.getPhoneNumber());
        dto.setAddress(person.getAddress());

        return dto;
    }

    @Transactional
    public UserProfileResponseDto updateProfile(
        User user,
        UpdateUserProfileRequestDto request
    ) {
        UserProfileResponseDto response = new UserProfileResponseDto(
            request.getFirstName(),
            request.getLastName(),
            request.getPhoneNumber(),
            request.getAddress()
        );

        Person person = user.getPerson();

        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setAddress(request.getAddress());

        personRepository.save(person);

        return response;
    }

    @Transactional
    public void changePassword(
        User user,
        ChangePasswordRequestDto request
    ) {
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);
    }

}
