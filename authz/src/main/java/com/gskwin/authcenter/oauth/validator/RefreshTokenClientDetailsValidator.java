package com.gskwin.authcenter.oauth.validator;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.oauth.OAuthTokenxRequest;

public class RefreshTokenClientDetailsValidator extends AbstractOauthTokenValidator {

	private static final Logger LOG = LoggerFactory.getLogger(RefreshTokenClientDetailsValidator.class);

	public RefreshTokenClientDetailsValidator(OAuthTokenxRequest oauthRequest) {
		super(oauthRequest);
	}

	/*
	* /oauth/token?client_id=mobile-client&client_secret=mobile&grant_type=refresh_token&refresh_token=b36f4978-a172-4aa8-af89-60f58abe3ba1
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

		//validate refresh_token
		final String refreshToken = tokenRequest.getRefreshToken();
		AccessToken accessToken = oauthService.loadAccessTokenByRefreshToken(refreshToken, oauthRequest.getClientId());
		if (accessToken == null || accessToken.refreshTokenExpired()) {
			LOG.debug("Invalid refresh_token: '{}'", refreshToken);
			return invalidRefreshTokenResponse(refreshToken);
		}

		return null;
	}

	private OAuthResponse invalidRefreshTokenResponse(String refreshToken) throws OAuthSystemException {
		return OAuthResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).setError(OAuthError.TokenResponse.INVALID_GRANT).setErrorDescription("Invalid refresh_token: " + refreshToken).buildJSONMessage();
	}

}
