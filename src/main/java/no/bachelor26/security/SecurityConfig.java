package no.bachelor26.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //TODO ordne javadocs på denne, authresponseDTO, LoginDTO, JwtAuthFilt, Authontroller + jwtservice
    //TODO fullføre jwtauthenticationfilter, authcontroller, jwtservice
    //TODO oppdater secconfing + test /apii/auth/login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(crsf -> crsf.disable())
                .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()               // MIDLERTIDIG FOR DEV; SLETT FØR PROD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        //.requestMatchers("/actuator/health").permitAll()
                        //.requestMatchers("/api/auth/**").permitAll()
                        //.anyRequest().authenticated()
                )
                // midlertidig enkel autentisering
                .httpBasic(httpBasic -> {});
        return http.build();
    }

}
