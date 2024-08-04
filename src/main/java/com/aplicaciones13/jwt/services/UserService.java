package com.aplicaciones13.jwt.services;

import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplicaciones13.jwt.models.EnumRole;
import com.aplicaciones13.jwt.models.Role;
import com.aplicaciones13.jwt.models.User;
import com.aplicaciones13.jwt.repository.RoleRepository;
import com.aplicaciones13.jwt.repository.UserRepository;
import com.aplicaciones13.jwt.services.impl.UserImpl;
import com.aplicaciones13.jwt.tools.Calc;
import com.aplicaciones13.jwt.tools.Generator;

/**
 * Clase UserDetailsServiceImpl
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  RoleRepository roleRepository;

  /**
   * Método para obtener todos los usuarios por like y de manera pageable
   * 
   * @param nameuser
   * @param pageable
   * @return
   */
  public Optional<User> getAllUsers(String nameuser, Pageable pageable) {
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
  public String generatePassword(Long id) {
    String password = Generator.generateRandom(Generator.LOWERCASE + Generator.UPPERCASE, 10);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));
    user.setPassword(encoder.encode(password));
    return password;
  }

  /**
   * Método para cambiar la clave de un usuario
   * 
   * @param pageable
   * @return Page<User>
   */
  public void changePassword(String username, String oldPassword, String newPassword) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    byte[] decodedBytes = Base64.getDecoder().decode(oldPassword);
    oldPassword = new String(decodedBytes);

    byte[] decodedBytesNew = Base64.getDecoder().decode(newPassword);
    newPassword = new String(decodedBytesNew);

    if (oldPassword.compareTo(newPassword) == 0) {
      throw new RuntimeException("Error: Old password is the same as the new password.");
    }

    user.setPassword(encoder.encode(newPassword));
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

    byte[] decodedBytes = Base64.getDecoder().decode(password);
    password = new String(decodedBytes);

    // Create new user's account
    User user = new User(
        username,
        email,
        encoder.encode(password),
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
          case "mod":
            Role modRole = roleRepository.findByName(EnumRole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Moderator Role is not found."));
            roles.add(modRole);

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
