package com.aplicaciones13.jwt.controllers;

import com.aplicaciones13.jwt.models.User;
import com.aplicaciones13.jwt.payload.request.ChangePasswordRequest;
import com.aplicaciones13.jwt.payload.request.SignupRequest;
import com.aplicaciones13.jwt.payload.response.MessageResponse;
import com.aplicaciones13.jwt.services.UserService;
import com.aplicaciones13.jwt.services.impl.UserImpl;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Método que obtiene todos los usuarios de manera pageable
     * 
     * @param pageable
     * @return
     */
    @GetMapping("/search/{nameuser}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getSearchUsers(@PathVariable String username, Pageable pageable) {
        Optional<User> usersOptional = userService.getAllUsers(username, pageable);
        if (usersOptional.isPresent()) {
            Page<User> users = (Page<User>) usersOptional.get();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Método que obtiene un usuario por su name de usuario
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable String username) {
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
        String newPassword = userService.generatePassword(id);
        return ResponseEntity.ok(new MessageResponse(newPassword));
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

        userService.changePassword(username, changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
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
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.toString()));
        }
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
        try {
            userService.registerUser(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword(),
                    signUpRequest.getRole());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.toString()));
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}