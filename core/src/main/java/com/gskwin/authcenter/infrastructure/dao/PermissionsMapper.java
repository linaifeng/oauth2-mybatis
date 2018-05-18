package com.gskwin.authcenter.infrastructure.dao;

import org.springframework.dao.DataAccessException;

import com.gskwin.authcenter.domain.oauth.Permission;

public interface PermissionsMapper {

	int deleteByPrimaryKey(String id);

	int insert(Permission record) throws DataAccessException;

	int insertSelective(Permission record) throws DataAccessException;

	Permission selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(Permission record);

	int updateByPrimaryKey(Permission record);

	Permission[] selectAllPermissions();

	Permission[] selectPermissionsByUsername(String username);
}