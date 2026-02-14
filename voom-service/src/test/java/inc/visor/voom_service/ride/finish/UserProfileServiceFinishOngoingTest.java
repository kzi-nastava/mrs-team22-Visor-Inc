package inc.visor.voom_service.ride.finish;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.person.service.UserProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceFinishOngoingTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;


    @Nested
    @DisplayName("getUserByEmail()")
    class GetUserByEmailTests {

        @Test
        @DisplayName("User with email exists")
        void testGetUserByEmail_Good() {
            String email = "valid@gmail.com";
            User mockUser = new User();
            mockUser.setEmail(email);
            mockUser.setId(1L);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

            User result = userProfileService.getUserByEmail(email);

            assertNotNull(result);
            assertEquals(email, result.getEmail());
            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("User doesnt exist")
        void testGetUserByEmail_Bad_NotFound() {
            String email = "unknown@gmail.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            User result = userProfileService.getUserByEmail(email);

            assertNull(result);
            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Email is null")
        void testGetUserByEmail_Edge_NullEmail() {

            String email = null;
            when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

            User result = userProfileService.getUserByEmail(email);

            assertNull(result);
        }

        @Test
        @DisplayName("Email is empty string")
        void testGetUserByEmail_BarelyValid_EmptyString() {
            String email = "";
            when(userRepository.findByEmail("")).thenReturn(Optional.empty());

            User result = userProfileService.getUserByEmail(email);

            assertNull(result);
        }

        @Test
        @DisplayName("Repository throws some exception")
        void testGetUserByEmail_BarelyInvalid_RepoError() {
            String email = "crash@gmail.com";
            when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("DB Connection failed"));

            assertThrows(RuntimeException.class, () -> {
                userProfileService.getUserByEmail(email);
            });
        }
    }
    
    
}