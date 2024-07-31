package com.aplicaciones13.jwt.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Clase que representa la peticion de refrescar token
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Data
public class TokenRefreshRequest {
  @NotBlank
  private String refreshToken;
}
