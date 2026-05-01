package com.aiblog.security;

import com.aiblog.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

  private final SecurityProperties properties;

  public JwtTokenService(SecurityProperties properties) {
    this.properties = properties;
  }

  public String issue(SecurityUser user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(properties.accessTokenMinutes(), ChronoUnit.MINUTES);
    return Jwts.builder()
        .subject(user.getUsername())
        .claim("uid", user.id())
        .claim("nickname", user.nickname())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(signingKey(), Jwts.SIG.HS256)
        .compact();
  }

  public String parseUsername(String token) {
    return parseClaims(token).getSubject();
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey signingKey() {
    byte[] key = properties.jwtSecret().getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(key);
  }
}
