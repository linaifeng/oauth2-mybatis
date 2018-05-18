package com.gskwin.authcenter.infrastructure.dao;

import com.gskwin.authcenter.domain.oauth.ClientDetails;

public interface ClientDetailsMapper {
	int deleteByPrimaryKey(String clientId);

	int insert(ClientDetails record);

	int saveClientDetails(ClientDetails record);

	ClientDetails selectByPrimaryKey(String clientId);

	int updateByPrimaryKeySelective(ClientDetails record);

	int updateByPrimaryKey(ClientDetails record);

	ClientDetails findClientDetails(String clientId);

}