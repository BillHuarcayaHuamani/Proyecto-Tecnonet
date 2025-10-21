package com.ProyectoTecnonet.tecnonet.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ProyectoTecnonet.tecnonet.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY = Keys
            .hmacShaKeyFor("tuLlaveSecretaMuyLargaDe256BitsParaQueSeaSegura1234567890".getBytes());
    private final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getIdUsuario())
                .claim("nombre", usuario.getNombre())
                .claim("apellido", usuario.getApellido())
                .claim("rol", usuario.getRol().getNombreRol())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(EXPIRATION_TIME, ChronoUnit.MILLIS)))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}