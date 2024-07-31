package com.aplicaciones13.jwt.payload.response;

import lombok.Data;

/**
 * Clase que representa la respuesta del token
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Data
public class TokenRefreshResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType = "Bearer";

  /**
   * Constructor de la clase
   * 
   * @param accessToken
   * @param refreshToken
   */
  public TokenRefreshResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
