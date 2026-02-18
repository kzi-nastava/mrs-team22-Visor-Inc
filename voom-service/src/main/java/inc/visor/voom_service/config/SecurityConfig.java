package inc.visor.voom_service.config;

import inc.visor.voom_service.auth.service.DevAuthenticationFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final ObjectProvider<JwtFilter> jwtFilterProvider;
    private final ObjectProvider<DevAuthenticationFilter> devFilterProvider;

    public SecurityConfig(
            AuthenticationProvider authenticationProvider,
            ObjectProvider<JwtFilter> jwtFilterProvider,
            ObjectProvider<DevAuthenticationFilter> devFilterProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtFilterProvider = jwtFilterProvider;
        this.devFilterProvider = devFilterProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsFilter corsFilter) throws Exception {

        http.addFilter(corsFilter)
                .csrf(AbstractHttpConfigurer::disable);

        DevAuthenticationFilter devFilter = devFilterProvider.getIfAvailable();
        JwtFilter jwtFilter = jwtFilterProvider.getIfAvailable();

        if (devFilter != null) {
            http.addFilterBefore(devFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

            return http.build();
        }

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider);

        if (jwtFilter != null) {
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }
}

