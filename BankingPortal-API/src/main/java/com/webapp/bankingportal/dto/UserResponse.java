package com.webapp.bankingportal.dto;


public class UserResponse  {
    
    private String name;
    private String email;
    private String address;
    private String phone_number;
    private String accountNumber;
    private String IFSC_code;
    private String branch;
    private String account_type;
	private boolean approved;
	private String userType; // New field
	private String residenceVerificationImageUrl; // URL to access the image
	private String idImageUrl;
    
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getIFSC_code() {
		return IFSC_code;
	}
	public void setIFSC_code(String iFSC_code) {
		IFSC_code = iFSC_code;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getAccount_type() {
		return account_type;
	}
	public void setAccount_type(String account_type) {
		this.account_type = account_type;
	}
	private String token;


	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getResidenceVerificationImageUrl() {
		return residenceVerificationImageUrl;
	}

	public void setResidenceVerificationImageUrl(String residenceVerificationImageUrl) {
		this.residenceVerificationImageUrl = residenceVerificationImageUrl;
	}

	public String getIdImageUrl() {
		return idImageUrl;
	}

	public void setIdImageUrl(String idImageUrl) {
		this.idImageUrl = idImageUrl;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
