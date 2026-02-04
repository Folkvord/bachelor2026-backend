package no.bachelor26.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(crsf -> crsf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        //admin område
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")


                        .anyRequest().authenticated()
                )
                // midlertidig enkel autentisering
                .httpBasic(httpBasic -> {});
        return http.build();
    }

    //in-memory user store brukt av spring security
    @Bean
    public InMemoryUserDetailsManager users(PasswordEncoder encoder) {
        //ctf spillere
        UserDetails player = User.withUsername("player")
                .password(encoder.encode("playerpass")) //passord lagres hashed
                .roles("PLAYER")
                .build();
        //admin, tilgang til admin only endpoints
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("adminpass"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(player, admin);
    }


    //password encoder brukt av spring security for hashing og verifisering
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
