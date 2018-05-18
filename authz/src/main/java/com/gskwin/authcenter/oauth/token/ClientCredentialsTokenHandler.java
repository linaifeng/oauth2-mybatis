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
import com.gskwin.authcenter.oauth.validator.ClientCredentialsClientDetailsValidator;
import com.gskwin.authcenter.web.WebUtils;

public class ClientCredentialsTokenHandler extends AbstractOAuthTokenHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ClientCredentialsTokenHandler.class);

	@Override
	public boolean support(OAuthTokenxRequest tokenRequest) throws OAuthProblemException {
		final String grantType = tokenRequest.getGrantType();
		return GrantType.CLIENT_CREDENTIALS.toString().equalsIgnoreCase(grantType);
	}

	/**
	 * /oauth/token?client_id=credentials-client&client_secret=credentials-secret&grant_type=client_credentials&scope=read
	 * write
	 * <p/>
	 * Response access_token If exist AccessToken and it is not expired, return
	 * it otherwise, return a new AccessToken
	 * <p/>
	 * {"access_token":"38187f32-e4fb-4650-8e4a-99903b66f20e","token_type":"bearer","expires_in":7}
	 */
	@Override
	public void handleAfterValidation() throws OAuthProblemException, OAuthSystemException {

		AccessToken accessToken = oauthService.retrieveClientCredentialsAccessToken(clientDetails(), tokenRequest.getScopes());
		updateLastLoginTime(accessToken.getTokenId());
		final OAuthResponse tokenResponse = createTokenResponse(accessToken, false);

		LOG.debug("'client_credentials' response: {}", tokenResponse);
		WebUtils.writeOAuthJsonResponse(response, tokenResponse);

	}

	@Override
	protected AbstractClientDetailsValidator getValidator() {
		return new ClientCredentialsClientDetailsValidator(tokenRequest);
	}

}
