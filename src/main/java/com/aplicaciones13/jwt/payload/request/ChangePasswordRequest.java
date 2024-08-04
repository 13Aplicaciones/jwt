package com.aplicaciones13.jwt.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * Clase que representa la peticion de cambio de contraseña
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Data
public class ChangePasswordRequest {
    
	@NotBlank
	private String oldPassword;

	@NotBlank
	@Size(min = 6, max = 40)
	private String newPassword;
}