package com.gskwin.authcenter.infrastructure.dao;

import org.apache.ibatis.annotations.Param;

import com.gskwin.authcenter.domain.oauth.AccessToken;

public interface AccessTokenMapper {

	AccessToken findAccessToken(@Param("clientId") String clientId, @Param("username") String username, @Param("authenticationId") String authenticationId);

	int deleteAccessToken(final AccessToken accessToken);

	int saveAccessToken(final AccessToken accessToken);

	AccessToken findAccessTokenByRefreshToken(@Param("refreshToken") String refreshToken, @Param("clientId") String clientId);

	AccessToken findAccessTokenByTokenId(String tokenId);

}