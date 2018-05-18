package com.gskwin.authcenter.oauth.authorize;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.oauth.OAuthAuthxRequest;
import com.gskwin.authcenter.oauth.validator.AbstractClientDetailsValidator;
import com.gskwin.authcenter.oauth.validator.TokenClientDetailsValidator;
import com.gskwin.authcenter.web.WebUtils;

public class TokenAuthorizeHandler extends AbstractAuthorizeHandler {

	private static final Logger LOG = LoggerFactory.getLogger(TokenAuthorizeHandler.class);

	public TokenAuthorizeHandler(OAuthAuthxRequest oauthRequest, HttpServletResponse response) {
		super(oauthRequest, response);
	}

	@Override
	protected AbstractClientDetailsValidator getValidator() {
		return new TokenClientDetailsValidator(oauthRequest);
	}

    /*
    *  response token
    *
    *  If it is the first logged or first approval , always return newly AccessToken
    *  Always exclude refresh_token
    *
    * */
	@Override
	protected void handleResponse() throws OAuthSystemException, IOException {

		if (forceNewAccessToken()) {
			forceTokenResponse();
		} else {
			AccessToken accessToken = oauthService.retrieveAccessToken(clientDetails(), this.currentUsername(request), oauthRequest.getScopes(), false);

			if (accessToken.tokenExpired()) {
				expiredTokenResponse(accessToken);
			} else {
				normalTokenResponse(accessToken);
			}
		}
	}

	private void forceTokenResponse() throws OAuthSystemException {
		AccessToken accessToken = oauthService.retrieveNewAccessToken(clientDetails(), this.currentUsername(request), oauthRequest.getScopes());
		normalTokenResponse(accessToken);
	}

	private void normalTokenResponse(AccessToken accessToken) throws OAuthSystemException {
		updateLastLoginTime(accessToken.getTokenId());
		final OAuthResponse oAuthResponse = createTokenResponse(accessToken, true);
		LOG.debug("'token' response: {}", oAuthResponse);

		WebUtils.writeOAuthQueryResponse(response, oAuthResponse);
	}

	private void expiredTokenResponse(AccessToken accessToken) throws OAuthSystemException {
		final ClientDetails clientDetails = clientDetails();
		LOG.debug("AccessToken {} is expired", accessToken);

		final OAuthResponse oAuthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
														.setError(OAuthError.ResourceResponse.EXPIRED_TOKEN)
														.setErrorDescription("access_token '" + accessToken.tokenId() + "' expired")
														.setErrorUri(clientDetails.getRedirectUri()).buildJSONMessage();

		WebUtils.writeOAuthJsonResponse(response, oAuthResponse);
	}

	private boolean forceNewAccessToken() {
		final ClientDetails clientDetails = clientDetails();
		if (clientDetails.trusted()) {
			return userFirstLogged;
		} else {
			return userFirstApproved;
		}
	}
}
