package com.gskwin.authcenter.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.common.StatusCode;
import com.gskwin.authcenter.domain.oauth.Permission;
import com.gskwin.authcenter.domain.shared.GuidGenerator;
import com.gskwin.authcenter.infrastructure.dao.PermissionsMapper;
import com.gskwin.authcenter.service.PermissionService;

@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {

	private static final Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);

	@Autowired
	private PermissionsMapper permissionsMapper;

	@Override
	public ResponseEntity addPermission(Permission permission) {
		Permission permissionDb = permissionsMapper.selectByPrimaryKey(permission.getId());
		if (permissionDb != null) {
			return new ResponseEntity(StatusCode.INVALID_REQUEST, "已经有此权限名");
		}

		permission.setId(GuidGenerator.generate());
		permission.setCreateTime(new Date());

		int count = 0;
		try {
			count = permissionsMapper.insertSelective(permission);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return new ResponseEntity(StatusCode.ERROR);
		}

		LOG.debug("Add permission's count:" + count);
		return new ResponseEntity(StatusCode.SUCCESS, "成功插入" + count + "条记录", permission.getId());
	}

	@Override
	public ResponseEntity getAllPermissions() {
		Permission[] permissions = permissionsMapper.selectAllPermissions();

		return new ResponseEntity(StatusCode.SUCCESS, "", permissions);
	}

	@Override
	public Permission[] getPermissionsByUsername(String username) {
		return permissionsMapper.selectPermissionsByUsername(username);
	}
}
