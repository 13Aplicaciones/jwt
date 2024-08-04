package com.aplicaciones13.jwt.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aplicaciones13.jwt.services.impl.UserImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;


/**
 * Clase JwtUtils
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Slf4j
@Component
public class JwtUtils {

  @Value("${aplicaciones13.jwt.secret}")
  private String jwtSecret;

  @Value("${aplicaciones13.jwt.expiration}")
  private int jwtExpirationMs;

  /**
   * Metodo generateJwtToken
   * 
   * @param userPrincipal
   * @return
   */
  public String generateJwtToken(UserImpl userPrincipal) {
    return generateTokenFromUsername(userPrincipal.getUsername());
  }

  /**
   * Metodo generateTokenFromUsername
   * 
   * @param username
   * @return
   */
  public String generateTokenFromUsername(String username) {
    return Jwts.builder().setSubject(username).setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  /**
   * Metodo getUserNameFromJwtToken
   * 
   * @param token
   * @return
   */
  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * Metodo validateJwtToken
   * 
   * @param authToken
   * @return
   */
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

}
