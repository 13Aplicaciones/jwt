package com.aplicaciones13.jwt.models;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import javax.persistence.UniqueConstraint;
import javax.persistence.JoinColumn;

import lombok.Data;

/**
 * Clase que representa los usuarios
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
@Data
@Entity
@Table(name = "users", uniqueConstraints = {
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email")
})
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 32)
	private String username;

	@NotBlank
	@Size(max = 64)
	@Email
	private String email;

	@NotBlank
	@Size(max = 128)
	private String password;

	@Temporal(TemporalType.DATE)
	@Column(name = "date_expiration_password")
	private Date dateExpirationPassword;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_expiration_credential")
	private Date dateExpirationCredential;

	@Column(name = "intent_failed")
	private int intentFailed;

	@Column(name = "status")
	private String status;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	/**
	 * Constructor vacio
	 */
	public User() {
	}

	/**
	 * Constructor vacio
	 *
	 * @param username
	 * @param email
	 * @param password
	 */
	public User(String username, String email, String password,
			Date dateExpirationPassword, Date dateExpirationCredential,
			int intentFailed, String status) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.dateExpirationPassword = dateExpirationPassword;
		this.dateExpirationCredential = dateExpirationCredential;
		this.intentFailed = intentFailed;
		this.status = status;
	}
}