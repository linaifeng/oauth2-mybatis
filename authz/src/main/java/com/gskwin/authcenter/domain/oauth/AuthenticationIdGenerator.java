package com.gskwin.authcenter.domain.oauth;

public interface AuthenticationIdGenerator {

	public String generate(String clientId, String username, String scope);

}