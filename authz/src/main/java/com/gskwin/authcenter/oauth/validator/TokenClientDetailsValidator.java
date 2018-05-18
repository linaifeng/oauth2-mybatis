package com.gskwin.authcenter.oauth.validator;

import java.util.Set;

import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gskwin.authcenter.domain.oauth.ClientDetails;

public class TokenClientDetailsValidator extends AbstractClientDetailsValidator {

	private static final Logger LOG = LoggerFactory.getLogger(TokenClientDetailsValidator.class);

	public TokenClientDetailsValidator(OAuthAuthzRequest oauthRequest) {
		super(oauthRequest);
	}

	/*
	 * grant_type="implicit"   -> response_type="token"
	 * ?response_type=token&scope=read,write&client_id=[client_id]&client_secret=[client_secret]&redirect_uri=[redirect_uri]
	* */
	@Override
	public OAuthResponse validateSelf(ClientDetails clientDetails) throws OAuthSystemException {

		//validate client_secret
		final String clientSecret = oauthRequest.getClientSecret();
		if (clientSecret == null || !clientSecret.equals(clientDetails.getClientSecret())) {
			return invalidClientSecretResponse();
		}

		//validate redirect_uri
		final String redirectURI = oauthRequest.getRedirectURI();
		if (redirectURI == null || !redirectURI.equals(clientDetails.getRedirectUri())) {
			LOG.debug("Invalid redirect_uri '{}' by response_type = 'code', client_id = '{}'", redirectURI, clientDetails.getClientId());
			return invalidRedirectUriResponse();
		}

		//validate scope
		final Set<String> scopes = oauthRequest.getScopes();
		if (scopes.isEmpty() || excludeScopes(scopes, clientDetails)) {
			return invalidScopeResponse();
		}

		return null;
	}

}
