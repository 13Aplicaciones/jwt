package com.aplicaciones13.jwt.controllers;

import java.util.Date;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.aplicaciones13.jwt.exception.TokenRefreshException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import com.aplicaciones13.jwt.payload.exception.ErrorMessage;

/**
 * Clase que maneja las excepciones de token
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@RestControllerAdvice
public class ControllerAdvice {

  /**
   * Metodo que maneja la excepciones de los controladores poner el mensaje de error de payload.
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

  /**
   * Metodo que maneja la excepcion de argumentos no validos
   * 
   * @param ex
   * @param request
   * @return ErrorMessage
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
    return new ErrorMessage(
        HttpStatus.BAD_REQUEST.value(),
        new Date(),
        ex.getMessage(),
        request.getDescription(false));
  }


  @ExceptionHandler(value = ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
    return new ErrorMessage(
        HttpStatus.BAD_REQUEST.value(),
        new Date(),
        ex.getMessage(),
        request.getDescription(false));
  }


  @ExceptionHandler(value = RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorMessage handleRuntimeException(RuntimeException ex, WebRequest request) {
    return new ErrorMessage(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        new Date(),
        ex.getMessage(),
        request.getDescription(false));
  }
}
