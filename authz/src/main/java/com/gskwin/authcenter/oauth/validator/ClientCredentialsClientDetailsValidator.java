package com.gskwin.authcenter.oauth.validator;

import java.util.Set;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.oauth.OAuthTokenxRequest;

public class ClientCredentialsClientDetailsValidator extends AbstractOauthTokenValidator {

	private static final Logger LOG = LoggerFactory.getLogger(ClientCredentialsClientDetailsValidator.class);

	public ClientCredentialsClientDetailsValidator(OAuthTokenxRequest oauthRequest) {
		super(oauthRequest);
	}

	/*
	* /oauth/token?client_id=credentials-client&client_secret=credentials-secret&grant_type=client_credentials&scope=read write
	* */
	@Override
	protected OAuthResponse validateSelf(ClientDetails clientDetails) throws OAuthSystemException {

		//validate grant_type
		final String grantType = grantType();
		if (!clientDetails.grantTypes().contains(grantType)) {
			LOG.debug("Invalid grant_type '{}', client_id = '{}'", grantType, clientDetails.getClientId());
			return invalidGrantTypeResponse(grantType);
		}

		//validate client_secret
		final String clientSecret = oauthRequest.getClientSecret();
		if (clientSecret == null || !clientSecret.equals(clientDetails.getClientSecret())) {
			LOG.debug("Invalid client_secret '{}', client_id = '{}'", clientSecret, clientDetails.getClientId());
			return invalidClientSecretResponse();
		}

		//validate scope
		final Set<String> scopes = oauthRequest.getScopes();
		if (scopes.isEmpty() || excludeScopes(scopes, clientDetails)) {
			return invalidScopeResponse();
		}

		return null;
	}

}
