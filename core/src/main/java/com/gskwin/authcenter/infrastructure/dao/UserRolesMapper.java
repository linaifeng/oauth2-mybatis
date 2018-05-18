package com.gskwin.authcenter.infrastructure.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface UserRolesMapper {
	int deleteByUserId(String userId);

	int deleteByRoleId(String roleId);

	int deleteByPrimaryKey(@Param("userId") String userId, @Param("roleId") String roleId);

	int insert(@Param("userId") String userId, @Param("roleId") String roleId) throws DataAccessException;

}