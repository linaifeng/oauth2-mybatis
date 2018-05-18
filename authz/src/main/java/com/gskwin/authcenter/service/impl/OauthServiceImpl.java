package com.gskwin.authcenter.service.impl;

import java.util.Set;

import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.domain.oauth.AuthenticationIdGenerator;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.domain.oauth.OauthCode;
import com.gskwin.authcenter.infrastructure.dao.AccessTokenMapper;
import com.gskwin.authcenter.infrastructure.dao.ClientDetailsMapper;
import com.gskwin.authcenter.infrastructure.dao.OauthCodeMapper;
import com.gskwin.authcenter.service.OauthService;

@Service("oauthService")
public class OauthServiceImpl implements OauthService {

	private static final Logger LOG = LoggerFactory.getLogger(OauthServiceImpl.class);

	@Autowired
	private AuthenticationIdGenerator authenticationIdGenerator;
	@Autowired
	private OAuthIssuer oAuthIssuer;
	@Autowired
	private AccessTokenMapper accessTokenMapper;
	@Autowired
	private ClientDetailsMapper clientDetailsMapper;
	@Autowired
	private OauthCodeMapper oauthCodeMapper;

	@Override
	public ClientDetails loadClientDetails(String clientId) {
		LOG.debug("Load ClientDetails by clientId: {}", clientId);
		return clientDetailsMapper.findClientDetails(clientId);
	}

	@Override
	public OauthCode saveAuthorizationCode(String authCode, ClientDetails clientDetails, String username) {
		OauthCode oauthCode = new OauthCode().code(authCode).username(username).clientId(clientDetails.getClientId());

		oauthCodeMapper.saveOauthCode(oauthCode);
		LOG.debug("Save OauthCode: {}", oauthCode);
		return oauthCode;
	}

	@Override
	public String retrieveAuthCode(ClientDetails clientDetails, String username) throws OAuthSystemException {
		final String clientId = clientDetails.getClientId();

		OauthCode oauthCode = oauthCodeMapper.findOauthCodeByUsernameClientId(username, clientId);
		if (oauthCode != null) {
			// Always delete exist
			LOG.debug("OauthCode ({}) is existed, remove it and create a new one", oauthCode);
			oauthCodeMapper.deleteOauthCode(oauthCode);
		}
		// create a new one
		oauthCode = createOauthCode(clientDetails, username);

		return oauthCode.code();
	}

	@Override
	public AccessToken retrieveAccessToken(ClientDetails clientDetails, String username, Set<String> scopes) throws OAuthSystemException {
		return retrieveAccessToken(clientDetails, username, scopes, clientDetails.supportRefreshToken());
	}

	@Override
	public AccessToken retrieveAccessToken(ClientDetails clientDetails, String username, Set<String> scopes, boolean includeRefreshToken) throws OAuthSystemException {
		String scope = OAuthUtils.encodeScopes(scopes);
		final String clientId = clientDetails.getClientId();

		final String authenticationId = authenticationIdGenerator.generate(clientId, username, scope);

		AccessToken accessToken = accessTokenMapper.findAccessToken(clientId, username, authenticationId);
		if (accessToken == null) {
			accessToken = createAndSaveAccessToken(clientDetails, includeRefreshToken, username, authenticationId);
			LOG.debug("Create a new AccessToken: {}", accessToken);
		}

		return accessToken;
	}

	// Always return new AccessToken, exclude refreshToken
	@Override
	public AccessToken retrieveNewAccessToken(ClientDetails clientDetails, String username, Set<String> scopes) throws OAuthSystemException {
		String scope = OAuthUtils.encodeScopes(scopes);
		final String clientId = clientDetails.getClientId();

		final String authenticationId = authenticationIdGenerator.generate(clientId, username, scope);

		AccessToken accessToken = accessTokenMapper.findAccessToken(clientId, username, authenticationId);
		if (accessToken != null) {
			LOG.debug("Delete existed AccessToken: {}", accessToken);
			accessTokenMapper.deleteAccessToken(accessToken);
		}
		accessToken = createAndSaveAccessToken(clientDetails, false, username, authenticationId);
		LOG.debug("Create a new AccessToken: {}", accessToken);

		return accessToken;
	}

	@Override
	public OauthCode loadOauthCode(String code, ClientDetails clientDetails) {
		final String clientId = clientDetails.getClientId();
		return oauthCodeMapper.findOauthCode(code, clientId);
	}

	@Override
	public boolean removeOauthCode(String code, ClientDetails clientDetails) {
		final OauthCode oauthCode = loadOauthCode(code, clientDetails);
		final int rows = oauthCodeMapper.deleteOauthCode(oauthCode);
		return rows > 0;
	}

	// Always return new AccessToken
	@Override
	public AccessToken retrieveNewAccessToken(ClientDetails clientDetails, String username) throws OAuthSystemException {
		final String clientId = clientDetails.getClientId();

		final String authenticationId = authenticationIdGenerator.generate(clientId, username, null);

		AccessToken accessToken = accessTokenMapper.findAccessToken(clientId, username, authenticationId);
		if (accessToken != null) {
			LOG.debug("Delete existed AccessToken: {}", accessToken);
			accessTokenMapper.deleteAccessToken(accessToken);
		}
		accessToken = createAndSaveAccessToken(clientDetails, clientDetails.supportRefreshToken(), username, authenticationId);
		LOG.debug("Create a new AccessToken: {}", accessToken);

		return accessToken;
	}

	// grant_type=password AccessToken
	@Override
	public AccessToken retrievePasswordAccessToken(ClientDetails clientDetails, Set<String> scopes, String username) throws OAuthSystemException {
		String scope = OAuthUtils.encodeScopes(scopes);
		final String clientId = clientDetails.getClientId();

		final String authenticationId = authenticationIdGenerator.generate(clientId, username, scope);
		AccessToken accessToken = accessTokenMapper.findAccessToken(clientId, username, authenticationId);

		boolean needCreate = false;
		if (accessToken == null) {
			needCreate = true;
			LOG.debug("Not found AccessToken from repository, will create a new one, client_id: {}", clientId);
		} else if (accessToken.tokenExpired()) {
			LOG.debug("Delete expired AccessToken: {} and create a new one, client_id: {}", accessToken, clientId);
			accessTokenMapper.deleteAccessToken(accessToken);
			needCreate = true;
		} else {
			LOG.info("Use existed AccessToken: {}, client_id: {}", accessToken, clientId);
		}

		if (needCreate) {
			accessToken = createAndSaveAccessToken(clientDetails, clientDetails.supportRefreshToken(), username, authenticationId);
			LOG.info("Create a new AccessToken: {}", accessToken);
		}

		return accessToken;
	}

	// grant_type=client_credentials
	@Override
	public AccessToken retrieveClientCredentialsAccessToken(ClientDetails clientDetails, Set<String> scopes) throws OAuthSystemException {
		String scope = OAuthUtils.encodeScopes(scopes);
		final String clientId = clientDetails.getClientId();
		// username = clientId

		final String authenticationId = authenticationIdGenerator.generate(clientId, clientId, scope);
		AccessToken accessToken = accessTokenMapper.findAccessToken(clientId, clientId, authenticationId);

		boolean needCreate = false;
		if (accessToken == null) {
			needCreate = true;
			LOG.debug("Not found AccessToken from repository, will create a new one, client_id: {}", clientId);
		} else if (accessToken.tokenExpired()) {
			LOG.debug("Delete expired AccessToken: {} and create a new one, client_id: {}", accessToken, clientId);
			accessTokenMapper.deleteAccessToken(accessToken);
			needCreate = true;
		} else {
			LOG.debug("Use existed AccessToken: {}, client_id: {}", accessToken, clientId);
		}

		if (needCreate) {
			// Ignore refresh_token
			accessToken = createAndSaveAccessToken(clientDetails, false, clientId, authenticationId);
			LOG.debug("Create a new AccessToken: {}", accessToken);
		}

		return accessToken;

	}

	@Override
	public AccessToken loadAccessTokenByRefreshToken(String refreshToken, String clientId) {
		LOG.debug("Load ClientDetails by refreshToken: {} and clientId: {}", refreshToken, clientId);
		return accessTokenMapper.findAccessTokenByRefreshToken(refreshToken, clientId);
	}

	/*
	 * Get AccessToken
	 * Generate a new AccessToken from existed(exclude token,refresh_token)
	 * Update access_token,refresh_token, expired.
	 * Save and remove old
	 */
	@Override
	public AccessToken changeAccessTokenByRefreshToken(String refreshToken, String clientId) throws OAuthSystemException {
		final AccessToken oldToken = loadAccessTokenByRefreshToken(refreshToken, clientId);

		AccessToken newAccessToken = oldToken.cloneMe();
		LOG.debug("Create new AccessToken: {} from old AccessToken: {}", newAccessToken, oldToken);

		ClientDetails details = clientDetailsMapper.findClientDetails(clientId);
		newAccessToken.updateByClientDetails(details);

		final String authId = authenticationIdGenerator.generate(clientId, oldToken.username(), null);
		newAccessToken.authenticationId(authId).tokenId(oAuthIssuer.accessToken()).refreshToken(oAuthIssuer.refreshToken());

		accessTokenMapper.deleteAccessToken(oldToken);
		LOG.debug("Delete old AccessToken: {}", oldToken);

		accessTokenMapper.saveAccessToken(newAccessToken);
		LOG.debug("Save new AccessToken: {}", newAccessToken);

		return newAccessToken;
	}

	@Override
	public AccessToken loadAccessTokenByTokenId(String tokenId) {
		return accessTokenMapper.findAccessTokenByTokenId(tokenId);
	}

	private AccessToken createAndSaveAccessToken(ClientDetails clientDetails, boolean includeRefreshToken, String username, String authenticationId) throws OAuthSystemException {
		AccessToken accessToken = new AccessToken().clientId(clientDetails.getClientId()).username(username).tokenId(oAuthIssuer.accessToken()).authenticationId(authenticationId)
				.updateByClientDetails(clientDetails);

		if (includeRefreshToken) {
			accessToken.refreshToken(oAuthIssuer.refreshToken());
		}

		accessTokenMapper.saveAccessToken(accessToken);
		LOG.debug("Save AccessToken: {}", accessToken);
		return accessToken;
	}

	private OauthCode createOauthCode(ClientDetails clientDetails, String username) throws OAuthSystemException {
		OauthCode oauthCode;
		final String authCode = oAuthIssuer.authorizationCode();

		LOG.debug("Save authorizationCode '{}' of ClientDetails '{}'", authCode, clientDetails);
		oauthCode = this.saveAuthorizationCode(authCode, clientDetails, username);
		return oauthCode;
	}

}
