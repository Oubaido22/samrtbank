package com.webapp.bankingportal.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String password;

	@Column(unique = true)
	private String email;
	private String userType;

	private String address;
	private String phone_number;
	private int otpRetryCount;
	private LocalDateTime lastOtpRequestTime;

	// Establishing a one-to-one relationship with the account
	@OneToOne(cascade = CascadeType.ALL)
	@JsonManagedReference
	private Account account;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role")
	private Set<String> roles = new HashSet<>();


	@Column(name = "approved")
	private boolean approved;

	@Lob  // Use Large Object type if storing as a BLOB
	private byte[] residenceVerificationImage;

	@Lob
	private byte[] idImage;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public Account getAccount() {
		return account;
	}

	// Convenience method to set the user's account
	public void setAccount(Account account) {
		this.account = account;
		account.setUser(this);
	}

	public int getOtpRetryCount() {
		return otpRetryCount;
	}

	public void setOtpRetryCount(int otpRetryCount) {
		this.otpRetryCount = otpRetryCount;
	}

	public LocalDateTime getLastOtpRequestTime() {
		return lastOtpRequestTime;
	}

	public void setLastOtpRequestTime(LocalDateTime lastOtpRequestTime) {
		this.lastOtpRequestTime = lastOtpRequestTime;
	}


	// getters and setters
	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public byte[] getIdImage() {
		return idImage;
	}

	public void setIdImage(byte[] idImage) {
		this.idImage = idImage;
	}

	public byte[] getResidenceVerificationImage() {
		return residenceVerificationImage;
	}

	public void setResidenceVerificationImage(byte[] residenceVerificationImage) {
		this.residenceVerificationImage = residenceVerificationImage;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
}
