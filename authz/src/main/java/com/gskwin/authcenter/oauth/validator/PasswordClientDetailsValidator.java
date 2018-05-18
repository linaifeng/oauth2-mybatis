package com.gskwin.authcenter.oauth.validator;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.domain.shared.BeanProvider;
import com.gskwin.authcenter.oauth.OAuthTokenxRequest;
import com.gskwin.authcenter.service.SecurityUtils;

public class PasswordClientDetailsValidator extends AbstractOauthTokenValidator {

	private static final Logger LOG = LoggerFactory.getLogger(PasswordClientDetailsValidator.class);

	private transient SecurityUtils securityUtils = BeanProvider.getBean(SecurityUtils.class);

	public PasswordClientDetailsValidator(OAuthTokenxRequest oauthRequest) {
		super(oauthRequest);
	}

	/*
	* /oauth/token?client_id=mobile-client&client_secret=mobile&grant_type=password&scope=read,write&username=mobile&password=mobile
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

		// validate username,password
		// if (invalidUsernamePassword()) {
		// return invalidUsernamePasswordResponse();
		// }
		final String username = tokenRequest.getUsername();
		final String password = tokenRequest.getPassword();
		try {
			securityUtils.login(username, password, tokenRequest.request().getSession());
		} catch (Exception e) {
			LOG.debug("Login failed by username: " + username, e);
			return OAuthResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).setError(OAuthError.TokenResponse.INVALID_GRANT).setErrorDescription(e.getMessage()).buildJSONMessage();
		}

		return null;
	}

}
