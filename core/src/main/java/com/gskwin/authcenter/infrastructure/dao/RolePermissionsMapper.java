package com.gskwin.authcenter.infrastructure.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface RolePermissionsMapper {
	int insert(@Param("roleId") String roleId, @Param("permissionId") String permissionId) throws DataAccessException;

	int deleteByRoleId(String roleId);

	int deleteByPermissionId(String permissionId);

	int deleteByPrimaryKey(@Param("roleId") String roleId, @Param("permissionId") String permissionId);
}