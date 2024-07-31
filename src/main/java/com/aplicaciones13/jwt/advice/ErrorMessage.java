package com.aplicaciones13.jwt.advice;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Clase que representa un mensaje de error
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Data
@AllArgsConstructor
public class ErrorMessage {
  private int statusCode;
  private Date timestamp;
  private String message;
  private String description;
}