package com.gskwin.authcenter.web;

import org.apache.oltu.oauth2.common.error.OAuthError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.infrastructure.AuthenticationException;

public class ValidateUtils {

	private static final Logger LOG = LoggerFactory.getLogger(ValidateUtils.class);

	public static void validateToken(String tokenId, AccessToken accessToken) throws AuthenticationException {
		if (tokenId == null || "".equals(tokenId) || accessToken == null) {
			LOG.debug("Invalid access_token: {}, because it is null", tokenId);
			throw new AuthenticationException("Invalid access_token: " + tokenId, OAuthError.ResourceResponse.INVALID_TOKEN);
		}
		if (accessToken.tokenExpired()) {
			LOG.debug("Invalid access_token: {}, because it is expired", tokenId);
			throw new AuthenticationException("Expired access_token: " + tokenId, OAuthError.ResourceResponse.EXPIRED_TOKEN);
		}
	}

	public static void validateClientDetails(String tokenId, AccessToken accessToken, ClientDetails clientDetails) throws AuthenticationException {
		if (clientDetails == null || clientDetails.archived()) {
			LOG.debug("Invalid ClientDetails: {} by client_id: {}, it is null or archived", clientDetails, accessToken.clientId());
			throw new AuthenticationException("Invalid client by token: " + tokenId, OAuthError.TokenResponse.INVALID_CLIENT);
		}
	}
}
