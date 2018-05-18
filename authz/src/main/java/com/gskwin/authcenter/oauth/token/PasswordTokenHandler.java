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
import com.gskwin.authcenter.oauth.validator.PasswordClientDetailsValidator;
import com.gskwin.authcenter.web.WebUtils;

public class PasswordTokenHandler extends AbstractOAuthTokenHandler {

	private static final Logger LOG = LoggerFactory.getLogger(PasswordTokenHandler.class);

	@Override
	public boolean support(OAuthTokenxRequest tokenRequest) throws OAuthProblemException {
		final String grantType = tokenRequest.getGrantType();
		return GrantType.PASSWORD.toString().equalsIgnoreCase(grantType);
	}

	@Override
	protected AbstractClientDetailsValidator getValidator() {
		return new PasswordClientDetailsValidator(tokenRequest);
	}

	/**
	 * /oauth/token?client_id=mobile-client&client_secret=mobile&grant_type=password&scope=read,write&username=mobile&password=mobile
	 * <p/>
	 * Response access_token
	 * If exist AccessToken and it is not expired, return it
	 * otherwise, return a new AccessToken(include refresh_token)
	 * <p/>
	 * {"token_type":"Bearer","expires_in":33090,"refresh_token":"976aeaf7df1ee998f98f15acd1de63ea","access_token":"7811aff100eb7dadec132f45f1c01727"}
	 */
	@Override
	public void handleAfterValidation() throws OAuthProblemException, OAuthSystemException {

		AccessToken accessToken = oauthService.retrievePasswordAccessToken(clientDetails(), tokenRequest.getScopes(), tokenRequest.getUsername());
		updateLastLoginTime(accessToken.getTokenId());
		final OAuthResponse tokenResponse = createTokenResponse(accessToken, false);

		LOG.debug("'password' response: {}", tokenResponse);
		WebUtils.writeOAuthJsonResponse(response, tokenResponse);

	}
}
