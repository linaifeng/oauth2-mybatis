package com.gskwin.authcenter.service;

import java.util.Set;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.domain.oauth.OauthCode;

public interface OauthService {

	ClientDetails loadClientDetails(String clientId);

	OauthCode saveAuthorizationCode(String authCode, ClientDetails clientDetails, String username);

	String retrieveAuthCode(ClientDetails clientDetails, String username) throws OAuthSystemException;

	AccessToken retrieveAccessToken(ClientDetails clientDetails, String username, Set<String> scopes) throws OAuthSystemException;

	AccessToken retrieveAccessToken(ClientDetails clientDetails, String username, Set<String> scopes, boolean includeRefreshToken) throws OAuthSystemException;

	// Always return new AccessToken, exclude refreshToken
	AccessToken retrieveNewAccessToken(ClientDetails clientDetails, String username, Set<String> scopes) throws OAuthSystemException;

	OauthCode loadOauthCode(String code, ClientDetails clientDetails);

	boolean removeOauthCode(String code, ClientDetails clientDetails);

	// Always return new AccessToken
	AccessToken retrieveNewAccessToken(ClientDetails clientDetails, String username) throws OAuthSystemException;

	// grant_type=password AccessToken
	AccessToken retrievePasswordAccessToken(ClientDetails clientDetails, Set<String> scopes, String username) throws OAuthSystemException;

	// grant_type=client_credentials
	AccessToken retrieveClientCredentialsAccessToken(ClientDetails clientDetails, Set<String> scopes) throws OAuthSystemException;

	AccessToken loadAccessTokenByRefreshToken(String refreshToken, String clientId);

	AccessToken changeAccessTokenByRefreshToken(String refreshToken, String clientId) throws OAuthSystemException;

	AccessToken loadAccessTokenByTokenId(String tokenId);

}
