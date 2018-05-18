package com.gskwin.authcenter.service.dto;

import java.io.Serializable;

public class LoginDto implements Serializable {

	private static final long serialVersionUID = -3577309264294738992L;
	private String username;
	private String password;

	public LoginDto() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
