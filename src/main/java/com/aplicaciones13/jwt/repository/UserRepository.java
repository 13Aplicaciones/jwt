package com.aplicaciones13.jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aplicaciones13.jwt.models.User;

/**
 * Repositorio de usuarios
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
  
  /**
   * Metodo que busca un usuario usuando like y cuyo estado sea activo (A) de manera pageable
   * 
   * @param username
   * @return
   */
  @Query("SELECT u FROM User u WHERE u.username LIKE %?1% AND u.status = 'A'")
  List<User> findByUsernameContaining(String username, Pageable pageable);

  /**
   * Metodo que busca un usuario por su nombre de usuario y cuyo estado sea activo
   * (A)
   * 
   * @param username
   * @return
   */
  @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.status = 'A'") 
  Optional<User> findByUsername(String username);

  /**
   * Metodo que busca un usuario por su id y cuyo estado sea activo (A)
   * 
   * @param id
   * @return
   */
  @Query("SELECT u FROM User u WHERE u.id = ?1 AND u.status = 'A'")
  Optional<User> findById(long id);

  /**
   * Metodo que verifica si el usuario existe por su id y cuyo estado sea activo
   * (A)
   * 
   * @param username
   * @return
   */
  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = ?1 AND u.status = 'A'")
  Boolean existsByIdActive(Long id);

  /**
   * Metodo que verifica si el usuario existe por su nombre de usuario y cuyo
   * estado sea activo (A)
   * 
   * @param username
   * @return
   */
  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = ?1 AND u.status = 'A'")
  Boolean existsByUsername(String username);

  /**
   * Metodo que verifica si el usuario existe por su email y cuyo estado sea
   * activo (A)
   * 
   * @param username
   * @return
   */
  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = ?1 AND u.status = 'A'")
  Boolean existsByEmail(String email);

  /**
   * Metodo para actualizar el estado del usuario
   * 
   * @param id
   */
  @Modifying
  @Query("UPDATE User u SET u.status = 'X' WHERE u.id = ?1")
  void deleteUser(Long id);
}
