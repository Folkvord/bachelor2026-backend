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

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "VGhpc0lzQVN1cGVyTG9uZ1NlY3JldEtleUZvckpXVFRlc3RpbmcxMjM0NTY3ODkw";

    private static final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 24;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {

        return null;
    }

    public String extractUsername(String token) {

        return null;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        return false;
    }

    private boolean isTokenExpired(String token) {

        return false;
    }

    private <T> T extractsClaim(String token, Function<Claims, T> claimsResolver) {

        return null;
    }
}
