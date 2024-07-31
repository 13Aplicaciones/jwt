package com.aplicaciones13.jwt.payload.response;

import java.util.List;

import lombok.Data;

/**
 * Clase que representa la respuesta del token
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Data
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String refreshToken;
	private Long id;
	private String username;
	private String email;
	private List<String> roles;

	/**
	 * Constructor de la clase
	 * 
	 * @param accessToken
	 * @param refreshToken
	 * @param id
	 * @param username
	 * @param email
	 * @param roles
	 */
	public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email, List<String> roles) {
		this.token = accessToken;
		this.refreshToken = refreshToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}
}
