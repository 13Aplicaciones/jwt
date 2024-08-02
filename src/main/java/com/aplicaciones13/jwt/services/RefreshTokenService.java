package com.aplicaciones13.jwt.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplicaciones13.jwt.exception.TokenRefreshException;
import com.aplicaciones13.jwt.models.RefreshToken;
import com.aplicaciones13.jwt.repository.RefreshTokenRepository;
import com.aplicaciones13.jwt.repository.UserRepository;

/**
 * Servicio de token de refresco
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Service
public class RefreshTokenService {
  @Value("${aplicaciones13.jwt.refresh_expiration}")
  private Long refreshTokenDurationMs;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  /**
   * Busca un token de refresco por su token
   * 
   * @param token Token de refresco
   * @return Token de refresco
   */
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  /**
   * Crea un token de refresco
   * 
   * @param userId Identificador del usuario
   * @return Token de refresco
   */
  public RefreshToken createRefreshToken(Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(userRepository.findById(userId).get());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }
   
  /**
   * Verifica si un token de refresco ha expirado
   * 
   * @param token Token de refresco
   * @return Token de refresco
   */
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
    }

    return token;
  }

  /**
   * Borra un token de refresco
   *
   * @param token Token de refresco
   */
  @Transactional
  public int deleteByUserId(Long userId) {
    return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
  }
}
