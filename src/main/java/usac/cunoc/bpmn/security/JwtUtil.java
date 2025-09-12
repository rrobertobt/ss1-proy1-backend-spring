package usac.cunoc.bpmn.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import usac.cunoc.bpmn.entity.User;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility class for secure token operations
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // For Spring Security we use email as the username; fall back to username claim, then subject (backward compatible)
    public String extractUsername(String token) {
        return extractClaim(token, claims -> {
            Object email = claims.get("email");
            if (email instanceof String) return (String) email;
            Object username = claims.get("username");
            if (username instanceof String) return (String) username;
            return claims.getSubject();
        });
    }

    public Integer extractUserId(String token) {
        String sub = extractClaim(token, Claims::getSubject);
        try {
            return sub != null ? Integer.valueOf(sub) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = buildUserClaims(user);
        // JWT spec defines subject as a string; store user id as string
        return createToken(claims, String.valueOf(user.getId()), jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = buildUserClaims(user);
        return createToken(claims, String.valueOf(user.getId()), refreshExpiration);
    }

    // Backward-compatible overloads (avoid breaking callers if any remain)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDetails.getUsername());
        return createToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDetails.getUsername());
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    private Map<String, Object> buildUserClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("type", user.getUserType() != null ? user.getUserType().getName() : null);
        return claims;
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractUsername(token);
            return (email != null && email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
