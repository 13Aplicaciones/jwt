package com.aplicaciones13.jwt.services;

import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplicaciones13.jwt.models.EnumRole;
import com.aplicaciones13.jwt.models.Role;
import com.aplicaciones13.jwt.models.User;
import com.aplicaciones13.jwt.repository.RoleRepository;
import com.aplicaciones13.jwt.repository.UserRepository;
import com.aplicaciones13.jwt.services.impl.UserImpl;
import com.aplicaciones13.jwt.tools.Calc;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase UserDetailsServiceImpl
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Slf4j
@Service
@Transactional
public class UserService implements UserDetailsService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  /**
   * Método para obtener todos los usuarios por like y de manera pageable
   * 
   * @param nameuser
   * @param pageable
   * @return
   */
  public List<User> getAllUsers(String nameuser, Pageable pageable) {
    return userRepository.findByUsernameContaining(nameuser, pageable);
  }

  /**
   * Método para obtener un unico usuario por su id
   * 
   * @param id
   * @return
   */
  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + username));
  }

  /**
   * Método para generar un password aleatorio
   * 
   * @param id
   * @return
   */
  public void generatePassword(Long id, String password) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));
    user.setPassword(password);
    user.setDateExpirationPassword(Calc.addDays(new Date(), 1));
    user.setDateExpirationCredential(Calc.addDays(new Date(), 365));
    userRepository.save(user);
  }

  /**
   * Método para convertir un password a base64
   * 
   * @param password
   * @return
   */
  public void validateChangePassword(String oldPassword, String newPassword) {
    if (oldPassword.equals(newPassword)) {
      throw new RuntimeException("Error: The new password is the same as the old password.");
    }
  }

  /**
   * Método para cambiar la clave de un usuario
   * 
   * @param pageable
   * @return Page<User>
   */
  public void changePassword(String username, String newPassword) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    user.setPassword(newPassword);
    user.setDateExpirationPassword(Calc.addDays(new Date(), 365));
    user.setDateExpirationCredential(Calc.addDays(new Date(), 720));
    userRepository.save(user);
  }

  /**
   * Metodo para borrado de usuario
   * 
   * @param id
   * @throws Exception
   */
  public void deleteUser(Long id) {
    Boolean existsById = userRepository.existsByIdActive(id);

    if (Boolean.FALSE.equals(existsById)) {
      throw new RuntimeException("Error: User is not found.");
    }

    userRepository.deleteUser(id);
  }

  /**
   * Método para crear un nuevo usuario
   * 
   * @param username
   * @param email
   * @param password
   * @param listRole
   * @throws Exception
   */
  public void registerUser(String username, String email, String password, Set<String> listRole) {
    Boolean existsByUsername = userRepository.existsByUsername(username);
    Boolean existsByEmail = userRepository.existsByEmail(email);

    if (Boolean.TRUE.equals(existsByUsername)) {
      throw new RuntimeException("Error: Username is already taken!");
    }

    if (Boolean.TRUE.equals(existsByEmail)) {
      throw new RuntimeException("Error: Email is already in use!");
    }

    // Create new user's account
    User user = new User(
        username,
        email,
        password,
        Calc.addDays(new Date(), 365),
        Calc.addDays(new Date(), 365),
        0,
        "A");

    Set<String> strRoles = listRole;
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(EnumRole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
            roles.add(adminRole);

            break;

          case "audid":
            Role audidRole = roleRepository.findByName(EnumRole.ROLE_AUDID)
                .orElseThrow(() -> new RuntimeException("Error: Audid Role is not found."));
            roles.add(audidRole);

            break;

          case "moderator":
            Role moderatorRole = roleRepository.findByName(EnumRole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Moderator Role is not found."));
            roles.add(moderatorRole);

            break;

          case "server":
            Role serverRole = roleRepository.findByName(EnumRole.ROLE_SERVER)
                .orElseThrow(() -> new RuntimeException("Error: Server Role is not found."));
            roles.add(serverRole);

            break;

          default:
            Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);
  }

  /**
   * Método que carga un usuario por su nombre de usuario
   * 
   * @param username Nombre de usuario
   * @return UserDetails
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserImpl.build(user);
  }
}
