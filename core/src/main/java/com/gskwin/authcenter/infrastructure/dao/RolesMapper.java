package com.gskwin.authcenter.infrastructure.dao;

import org.springframework.dao.DataAccessException;

import com.gskwin.authcenter.domain.oauth.Role;

public interface RolesMapper {
	int deleteByPrimaryKey(String id);

	int insert(Role record) throws DataAccessException;

	int insertSelective(Role record) throws DataAccessException;

	Role selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(Role record);

	int updateByPrimaryKey(Role record);

	Role[] selectAllRoles();

	Role selectByName(String roleName);

	Role[] selectRolesByUserId(String userId);
}