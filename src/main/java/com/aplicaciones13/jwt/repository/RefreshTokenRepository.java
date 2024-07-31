package com.aplicaciones13.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.aplicaciones13.jwt.models.RefreshToken;
import com.aplicaciones13.jwt.models.User;

/**
 * Repositorio de token de refresco
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);
  
  @Modifying
  int deleteByUser(User user);
}
