package com.aplicaciones13.jwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Clase que maneja las excepciones de token
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   * 
   * @param token
   * @param message
   */
  public TokenRefreshException(String token, String message) {
    super(String.format("Failed for [%s]: %s", token, message));
  }
}
