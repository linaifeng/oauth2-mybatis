package com.gskwin.authcenter.service;

import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.domain.oauth.Permission;

public interface PermissionService {

	ResponseEntity addPermission(Permission permission);

	ResponseEntity getAllPermissions();

	Permission[] getPermissionsByUsername(String username);
}
