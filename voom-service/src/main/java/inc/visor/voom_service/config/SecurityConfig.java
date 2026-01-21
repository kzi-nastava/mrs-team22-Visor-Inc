package inc.visor.voom_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import inc.visor.voom_service.auth.service.DevAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtFilter;

    private final DevAuthenticationFilter devAuthenticationFilter;

    public SecurityConfig(
            AuthenticationProvider authenticationProvider,
            JwtFilter jwtFilter,
            DevAuthenticationFilter devAuthenticationFilter
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtFilter = jwtFilter;
        this.devAuthenticationFilter = devAuthenticationFilter;
    }

    @Value("${security.disable-auth:false}")
    private boolean disableAuth;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsFilter corsFilter) throws Exception {
        http
                .addFilter(corsFilter)
                .csrf(AbstractHttpConfigurer::disable);

        if (disableAuth) {
            http
                    .addFilterBefore(devAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/auth/**"
                ).permitAll()
                .requestMatchers("/api/**", "/ws/**",
                        "/topic/**",
                        "/app/**").permitAll()
        ).sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
        ).authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
