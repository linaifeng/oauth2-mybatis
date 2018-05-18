package com.gskwin.authcenter.service;

import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.domain.oauth.User;

public interface UserService {
	ResponseEntity addUser(User user);

	ResponseEntity grantRoleForUser(String userId, String roleId);

	User getUserInfoByTokenId(String tokenId);

	User getUserInfoByUsername(String username);

	ResponseEntity deleteUser(String userId);

	ResponseEntity editPassword(String userId, String oldPassword, String newPassword);

	ResponseEntity editPassword(String userId, String newPassword);

	ResponseEntity editUsername(String userId, String username);

	ResponseEntity getUsersForRole(String roleName);

	int updateByPrimaryKeySelective(User record);
}
