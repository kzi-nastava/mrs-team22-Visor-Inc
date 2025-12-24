package inc.visor.voom_service.authorized.user.profile.service;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.authorized.user.profile.dto.ChangePasswordRequestDto;
import inc.visor.voom_service.authorized.user.profile.dto.UpdateUserProfileRequestDto;
import inc.visor.voom_service.authorized.user.profile.dto.UserProfileResponseDto;
import inc.visor.voom_service.domain.model.Person;

@Service
public class UserProfileService {

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

    public UserProfileResponseDto updateProfile(
        User user,
        UpdateUserProfileRequestDto dto
    ) {
        UserProfileResponseDto response = new UserProfileResponseDto(
            user.getEmail(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getPhoneNumber(),
            dto.getAddress()
        );

        return response;
    }

    public void changePassword(
        User user,
        ChangePasswordRequestDto request
    ) {
        // TODO: implement password change logic
    }

}
