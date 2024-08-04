package com.aplicaciones13.jwt.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 120)
	private String password;

}
