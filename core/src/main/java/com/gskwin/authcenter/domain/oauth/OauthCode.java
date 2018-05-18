package com.gskwin.authcenter.domain.oauth;

import com.gskwin.authcenter.domain.AbstractDomain;

public class OauthCode extends AbstractDomain {

	private static final long serialVersionUID = 2444676328989650208L;

	private String code;

	private String username;

	private String clientId;

	public OauthCode() {
	}

	public String code() {
		return code;
	}

	public OauthCode code(String code) {
		this.code = code;
		return this;
	}

	public String username() {
		return username;
	}

	public OauthCode username(String username) {
		this.username = username;
		return this;
	}

	public String clientId() {
		return clientId;
	}

	public OauthCode clientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

}
