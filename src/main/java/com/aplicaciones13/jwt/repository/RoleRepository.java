package com.aplicaciones13.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aplicaciones13.jwt.models.ERole;
import com.aplicaciones13.jwt.models.Role;

/**
 * Repositorio de roles
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
