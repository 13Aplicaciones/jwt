package com.aplicaciones13.jwt.controllers;

import com.aplicaciones13.jwt.models.User;
import com.aplicaciones13.jwt.payload.request.ChangePasswordRequest;
import com.aplicaciones13.jwt.payload.request.SignupRequest;
import com.aplicaciones13.jwt.payload.response.MessageResponse;
import com.aplicaciones13.jwt.services.UserService;
import com.aplicaciones13.jwt.services.impl.UserImpl;
import com.aplicaciones13.jwt.tools.Generator;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Clase para el controlador de usuarios
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Tag(name = "Users", description = "Controlador de usuarios")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder encoder;

    /**
     * Método que obtiene todos los usuarios de manera pageable
     * 
     * @param pageable
     * @return
     */
    @GetMapping("/search/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getSearchUsers(@PathVariable String username, Pageable pageable) {
        List<User> listUser = userService.getAllUsers(username, pageable);

        if (listUser.size() > 0) {
            return new ResponseEntity<>(listUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Método que obtiene un usuario por su name de usuario
     * 
     * @param username
     * @return
     */
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Metodo para generar un password aleatorio de parte de los administradores
     * 
     * @param changePasswordRequest
     * @return
     */
    @PutMapping("/generatePassword/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generatePassword(@PathVariable Long id) {
        String password = Generator.generateRandom(Generator.LOWERCASE + Generator.UPPERCASE, 10);
        userService.generatePassword(id, convertPassword(password));
        return ResponseEntity.ok(new MessageResponse(password));
    }

    /**
     * Método que cambia la contraseña de un usuario
     * 
     * @param changePasswordRequest
     * @return
     */
    @PutMapping("/changePassword")
    public ResponseEntity<User> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        UserImpl userImpl = (UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userImpl.getUsername();

        byte[] decodedBytes = Base64.getDecoder().decode(changePasswordRequest.getOldPassword());
        String oldPassword = new String(decodedBytes);

        byte[] decodedBytesNew = Base64.getDecoder().decode(changePasswordRequest.getNewPassword());
        String newPassword = new String(decodedBytesNew);

        userService.validateChangePassword(oldPassword, newPassword);
        userService.changePassword(username, convertPassword(newPassword));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Método que elimina a un usuario solo si tienes el rol de ADMIN
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Método que registra a un usuario solo si tienes el rol de ADMIN
     * 
     * @param signUpRequest
     * @return
     */
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        userService.registerUser(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                convertPassword(signUpRequest.getPassword()),
                signUpRequest.getRoles());

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * Método que convierte el password de base64 a un password encriptado
     * 
     * @param passwordBase64
     * @return
     */
    private String convertPassword(String passwordBase64) {
        byte[] decodedBytes = Base64.getDecoder().decode(passwordBase64);
        String password = new String(decodedBytes);
        return encoder.encode(password);
    }
}