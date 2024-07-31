package com.aplicaciones13.jwt.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Clase que representa la peticion de login
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Data
public class LoginRequest {
	@NotBlank
	private String username;

	@NotBlank
	private String password;

}
