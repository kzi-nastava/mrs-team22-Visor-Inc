package inc.visor.voom_service.auth.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import inc.visor.voom_service.auth.user.model.Permission;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class DevAuthenticationFilter extends OncePerRequestFilter {

    @Value("${security.disable-auth:false}")
    private boolean disableAuth;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (disableAuth && SecurityContextHolder.getContext().getAuthentication() == null) {

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

        user.setId(2L);
        user.setEmail("driver2@gmail.com");
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
