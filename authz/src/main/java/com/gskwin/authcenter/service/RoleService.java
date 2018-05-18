package com.gskwin.authcenter.service;

import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.domain.oauth.Role;

public interface RoleService {

	ResponseEntity addRole(Role role);

	ResponseEntity getAllRoles();
	
	ResponseEntity getRolesForUser(String userId);

	ResponseEntity grantPermissionForRole(String roleId, String permissionId);
}
