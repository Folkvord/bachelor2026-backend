package no.bachelor26.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import no.bachelor26.DTO.AuthResponseDTO;
import no.bachelor26.DTO.LoginDTO;
import no.bachelor26.security.JwtService;

/**
 * Kontroller for autentisering (login).
 *
 * Tar imot brukernavn + passord
 * Autentiserer bruker via Spring Security
 * Returnerer JWT token ved vellykket login
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    /**
     * Logger inn en bruker og returnerer JWT token
     *
     * @param loginDTO inneholder brukernavn + passord
     * @return JWT token hvis autentisering lykkes
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
