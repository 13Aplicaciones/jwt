package com.aplicaciones13.jwt.advice;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.aplicaciones13.jwt.exception.TokenRefreshException;

/**
 * Clase que maneja las excepciones de token
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@RestControllerAdvice
public class TokenControllerAdvice {

  /**
   * Metodo que maneja la excepcion de token
   * 
   * @param ex
   * @param request
   * @return ErrorMessage
   */
  @ExceptionHandler(value = TokenRefreshException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorMessage handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
    return new ErrorMessage(
        HttpStatus.FORBIDDEN.value(),
        new Date(),
        ex.getMessage(),
        request.getDescription(false));
  }
}
