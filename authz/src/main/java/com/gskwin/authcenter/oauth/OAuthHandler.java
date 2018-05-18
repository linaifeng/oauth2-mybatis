package com.gskwin.authcenter.oauth;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.domain.oauth.User;
import com.gskwin.authcenter.domain.shared.BeanProvider;
import com.gskwin.authcenter.service.OauthService;
import com.gskwin.authcenter.service.SecurityUtils;
import com.gskwin.authcenter.service.UserService;

public abstract class OAuthHandler {

	private static final Logger LOG = LoggerFactory.getLogger(OAuthHandler.class);

	protected transient OauthService oauthService = BeanProvider.getBean(OauthService.class);
	protected transient SecurityUtils securityUtils = BeanProvider.getBean(SecurityUtils.class);
	protected transient UserService userService = BeanProvider.getBean(UserService.class);

	private ClientDetails clientDetails;

	protected ClientDetails clientDetails() {
		if (clientDetails == null) {
			final String clientId = clientId();
			clientDetails = oauthService.loadClientDetails(clientId);
			LOG.debug("Load ClientDetails: {} by clientId: {}", clientDetails, clientId);
		}
		return clientDetails;
	}

	protected String currentUsername(HttpServletRequest request) {
		return securityUtils.getPrincipal(request.getSession());
	}

	/**
	 * Create AccessToken response
	 *
	 * @param accessToken
	 *            AccessToken
	 * @param queryOrJson
	 *            True is QueryMessage, false is JSON message
	 * @return OAuthResponse
	 * @throws org.apache.oltu.oauth2.common.exception.OAuthSystemException
	 */
	protected OAuthResponse createTokenResponse(AccessToken accessToken, boolean queryOrJson)
			throws OAuthSystemException {
		final ClientDetails clientDetails = clientDetails();

		final OAuthASResponse.OAuthTokenResponseBuilder builder = OAuthASResponse
				.tokenResponse(HttpServletResponse.SC_OK).location(clientDetails.getRedirectUri())
				.setAccessToken(accessToken.tokenId())
				.setExpiresIn(String.valueOf(accessToken.currentTokenExpiredSeconds()))
				.setTokenType(accessToken.tokenType());

		final String refreshToken = accessToken.refreshToken();
		if (StringUtils.isNotEmpty(refreshToken)) {
			builder.setRefreshToken(refreshToken);
		}

		return queryOrJson ? builder.buildQueryMessage() : builder.buildJSONMessage();
	}

	protected abstract String clientId();

	protected void updateLastLoginTime(String tokenId) {
		String id = userService.getUserInfoByTokenId(tokenId).getId();
		User user = new User();
		user.setLastLoginTime(new Date());
		user.setId(id);
		userService.updateByPrimaryKeySelective(user);
	}
}
