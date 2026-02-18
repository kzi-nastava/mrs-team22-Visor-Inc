package inc.visor.voom_service.auth.service;

import inc.visor.voom_service.auth.user.model.*;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Set;

@Component
@Order(1)
@ConditionalOnProperty(
        name = "security.disable-auth",
        havingValue = "true"
)
public class DevAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                User fakeUser = createFakeUser();

                VoomUserDetails fakeUserDetails = new VoomUserDetails(fakeUser);

                UsernamePasswordAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(
                        fakeUserDetails,
                        null,
                        fakeUserDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        } catch (java.io.IOException e) {
            throw new ServletException(e);
        }
    }

    private User createFakeUser() {
        User user = new User();

        user.setId(6L);
        user.setEmail("user1@gmail.com");
        user.setPassword("N/A");
        user.setUserStatus(UserStatus.ACTIVE);

        UserRole role = new UserRole();
        role.setRoleName("USER");

        Permission read = new Permission();
        read.setPermissionName("READ_PROFILE");

        Permission update = new Permission();
        update.setPermissionName("UPDATE_PROFILE");

        role.setPermissions(Set.of(read, update));

        user.setUserRole(role);

        return user;
    }
}
