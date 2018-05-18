package com.gskwin.authcenter.oauth.token;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.oauth.OAuthTokenxRequest;
import com.gskwin.authcenter.oauth.validator.AbstractClientDetailsValidator;
import com.gskwin.authcenter.oauth.validator.AuthorizationCodeClientDetailsValidator;
import com.gskwin.authcenter.web.WebUtils;

public class AuthorizationCodeTokenHandler extends AbstractOAuthTokenHandler {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationCodeTokenHandler.class);

	@Override
	public boolean support(OAuthTokenxRequest tokenRequest) throws OAuthProblemException {
		final String grantType = tokenRequest.getGrantType();
		return GrantType.AUTHORIZATION_CODE.toString().equalsIgnoreCase(grantType);
	}

	/*
	*
	* /oauth/token?client_id=unity-client&client_secret=unity&grant_type=authorization_code&code=zLl170&redirect_uri=redirect_uri
	* */
	@Override
	public void handleAfterValidation() throws OAuthProblemException, OAuthSystemException {

		//response token, always new
		responseToken();

		//remove code lastly
		removeCode();
	}

	private void removeCode() {
		final String code = tokenRequest.getCode();
		final boolean result = oauthService.removeOauthCode(code, clientDetails());
		LOG.debug("Remove code: {} result: {}", code, result);
	}

	private void responseToken() throws OAuthSystemException {
		AccessToken accessToken = oauthService.retrieveNewAccessToken(clientDetails(), this.currentUsername(request));
		updateLastLoginTime(accessToken.getTokenId());
		final OAuthResponse tokenResponse = createTokenResponse(accessToken, false);

		LOG.debug("'authorization_code' response: {}", tokenResponse);
		WebUtils.writeOAuthJsonResponse(response, tokenResponse);
	}

	@Override
	protected AbstractClientDetailsValidator getValidator() {
		return new AuthorizationCodeClientDetailsValidator(tokenRequest);
	}

}
