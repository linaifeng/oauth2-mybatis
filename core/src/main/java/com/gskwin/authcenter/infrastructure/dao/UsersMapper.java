package com.gskwin.authcenter.infrastructure.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;
import com.gskwin.authcenter.domain.oauth.User;

public interface UsersMapper {
	int deleteByPrimaryKey(String id);

	int insert(User record) throws DataAccessException;

	int insertSelective(User record) throws DataAccessException;

	User selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(User record);

	int updateByPrimaryKey(User record);

	public User getUserInfoByTokenId(String tokenId);

	public User selectByUsername(String username);

	int editPassword(@Param("id") String id, @Param("password") String password);

	int editUsername(@Param("id") String id, @Param("username") String username);

	String selectPasswordByPrimaryKey(String id);

	User[] getUsersForRole(String roleName);
}