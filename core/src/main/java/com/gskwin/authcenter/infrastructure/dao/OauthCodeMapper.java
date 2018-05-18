package com.gskwin.authcenter.infrastructure.dao;

import org.apache.ibatis.annotations.Param;

import com.gskwin.authcenter.domain.oauth.OauthCode;

public interface OauthCodeMapper {
	int insert(OauthCode record);

	int saveOauthCode(OauthCode record);

	OauthCode findOauthCode(@Param("code") String code, @Param("clientId") String clientId);

	OauthCode findOauthCodeByUsernameClientId(@Param("username") String username, @Param("clientId") String clientId);

	int deleteOauthCode(final OauthCode oauthCode);
}