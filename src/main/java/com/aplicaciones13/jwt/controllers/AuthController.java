package com.aplicaciones13.jwt.controllers;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import java.util.Base64;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aplicaciones13.jwt.exception.TokenRefreshException;
import com.aplicaciones13.jwt.models.EnumRole;
import com.aplicaciones13.jwt.models.RefreshToken;
import com.aplicaciones13.jwt.models.Role;
import com.aplicaciones13.jwt.models.User;
import com.aplicaciones13.jwt.payload.request.LoginRequest;
import com.aplicaciones13.jwt.payload.request.SignupRequest;
import com.aplicaciones13.jwt.payload.request.TokenRefreshRequest;
import com.aplicaciones13.jwt.payload.response.JwtResponse;
import com.aplicaciones13.jwt.payload.response.MessageResponse;
import com.aplicaciones13.jwt.payload.response.TokenRefreshResponse;
import com.aplicaciones13.jwt.repository.RoleRepository;
import com.aplicaciones13.jwt.repository.UserRepository;
import com.aplicaciones13.jwt.security.jwt.JwtUtils;
import com.aplicaciones13.jwt.services.RefreshTokenService;
import com.aplicaciones13.jwt.services.UserDetailsImpl;
import com.aplicaciones13.jwt.tools.Calculos;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que controla la autenticación de los usuarios
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Tag(name = "Auth", description = "Controlador de autenticación")
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;

  /**
   * Método que autentica a un usuario
   * 
   * @param loginRequest
   * @return
   */

  @PostMapping("/signIn")
  @Operation(operationId = "signIn", description = "Autenticación de usuario", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class))), responses = {
      @ApiResponse(responseCode = "200", description = "Contenedor de la respuesta de autenticación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))) })
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    byte[] decodedBytes = Base64.getDecoder().decode(loginRequest.getPassword());
    loginRequest.setPassword(new String(decodedBytes));  

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(userDetails);

    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
        .collect(Collectors.toList());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
        userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  /**
   * Método que registra a un usuario
   * 
   * @param signUpRequest
   * @return
   */
  @PostMapping("/signUp")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    Boolean existsByUsername = userRepository.existsByUsername(signUpRequest.getUsername());
    Boolean existsByEmail = userRepository.existsByEmail(signUpRequest.getEmail());

    if (Boolean.TRUE.equals(existsByUsername)) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (Boolean.TRUE.equals(existsByEmail)) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    byte[] decodedBytes = Base64.getDecoder().decode(signUpRequest.getPassword());
    signUpRequest.setPassword(new String(decodedBytes));  

    // Create new user's account
    User user = new User(
        signUpRequest.getUsername(), 
        signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()),
        Calculos.addDays(new Date(), 365),
        Calculos.addDays(new Date(), 365),
        0,
        "A"
        );

    Set<String> strRoles = signUpRequest.getRole();
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

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  /**
   * Método que refresca el token
   * 
   * @param request
   * @return
   */
  @PostMapping("/refreshToken")
  @PreAuthorize("hasRole( 'USER')")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        })
        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
            "Refresh token is not in database!"));
  }

  /**
   * Método que cierra la sesión de un usuario
   * 
   * @return
   */
  @PostMapping("/signOut")
  @PreAuthorize("hasAnyRole('USER')")
  public ResponseEntity<?> logoutUser() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();
    refreshTokenService.deleteByUserId(userId);
    return ResponseEntity.ok(new MessageResponse("Log out successful!"));
  }
}
