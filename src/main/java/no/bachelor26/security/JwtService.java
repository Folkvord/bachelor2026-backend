package no.bachelor26.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Tjeneste for håndtering av JWT (JSON Web Tokens)
 *!!!SECRET_KEY er hardkoda, må endres!!!
 * Ansvar:
 * Genererer tokens ved innlogging
 * Hente informasjon fra tokens
 * validere tokens
 *
 * Brukes av autentiseringssystemet for å sikre API endepunkter
 * @Author Edwina Larsen
 */
@Service
public class JwtService {
//midlertidig SECRET_KEY, skal legges i miljø
    private static final String SECRET_KEY =
            "VGhpc0lzQVN1cGVyTG9uZ1NlY3JldEtleUZvckpXVFRlc3RpbmcxMjM0NTY3ODkw";

    private static final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 24;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genererer en JWT for en autentisert bruker.
     *
     * @param userDetails info om brukeren
     * @return signert JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Henter brukernavn (subject) fra JWT-token
     * @param token JWT token
     * @return brukernavn lagret i tokenet
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Sjekker om JWT token er gyldig
     * (hvis brukernavn matcher + token ikke er utløpt)
     *
     * @param token JWT token
     * @param userDetails brukerdata
     * @return true hvis token er gyldig
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolver.apply(claims);
    }
}