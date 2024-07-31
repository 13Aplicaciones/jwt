package com.aplicaciones13.jwt.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Clase que controla las rutas de prueba
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

	/**
	 * Método que regresa un mensaje de contenido público
	 * 
	 * @return
	 */
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}

	/**
	 * Método que regresa un mensaje de contenido de usuario
	 * 
	 * @return
	 */
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	/**
	 * Método que regresa un mensaje de contenido de usuario actual
	 * 
	 * @return
	 */
	@GetMapping("/user-current")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccessCurrent() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return "User Content for " + username + ".";
	}

	/**
	 * Método que regresa un mensaje de contenido de moderador
	 * 
	 * @return
	 */
	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	/**
	 * Método que regresa un mensaje de contenido de administrador
	 * 
	 * @return
	 */
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}
}
