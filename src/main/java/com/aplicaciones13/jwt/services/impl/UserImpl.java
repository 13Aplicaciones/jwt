package com.aplicaciones13.jwt.services.impl;

import java.util.Date;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.aplicaciones13.jwt.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase UserDetailsImpl
 * 
 * Clase personalizada que implementa la interfaz UserDetails de Spring Security
 * para la autenticación de los usuarios
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Slf4j
public class UserImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String email;

	@JsonIgnore
	private String password;

	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	private Collection<? extends GrantedAuthority> authorities;

	/**
	 * Constructor de la clase
	 * 
	 * @param id
	 * @param username
	 * @param email
	 * @param password
	 * @param authorities
	 */
	public UserImpl(Long id, String username, String email, String password,
			boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled,
			Collection<? extends GrantedAuthority> authorities) {

		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;

		this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;

		this.enabled = enabled;
		this.authorities = authorities;
	}

	/**
	 * Método que construye un UserDetailsImpl
	 * 
	 * @param user
	 * @return UserDetailsImpl
	 */
	public static UserImpl build(User user) {
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());

		return new UserImpl(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getPassword(),
				user.getDateExpirationPassword().after(new Date()),
				user.getIntentFailed() < 3,
				user.getDateExpirationCredential().after(new Date()),
				user.getStatus().equals("A"),
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserImpl user = (UserImpl) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
