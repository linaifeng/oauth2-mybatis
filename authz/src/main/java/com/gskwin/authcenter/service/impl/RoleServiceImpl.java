package com.gskwin.authcenter.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.common.StatusCode;
import com.gskwin.authcenter.domain.oauth.Role;
import com.gskwin.authcenter.domain.shared.GuidGenerator;
import com.gskwin.authcenter.infrastructure.dao.RolePermissionsMapper;
import com.gskwin.authcenter.infrastructure.dao.RolesMapper;
import com.gskwin.authcenter.service.RoleService;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

	private static final Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);
	@Autowired
	private RolesMapper rolesMapper;
	@Autowired
	private RolePermissionsMapper rolePermissionsMapper;

	@Override
	public ResponseEntity addRole(Role role) {
		// 验证roleName唯一性
		Role roleDb = rolesMapper.selectByName(role.getRoleName());
		if (roleDb != null) {
			return new ResponseEntity(StatusCode.INVALID_REQUEST, "已经有此角色名");
		}

		// 创建role
		role.setId(GuidGenerator.generate());
		role.setCreateTime(new Date());

		int count = 0;
		try {
			count = rolesMapper.insertSelective(role);
		} catch (DataAccessException e) {
			e.printStackTrace();

			return new ResponseEntity(StatusCode.ERROR);
		}

		LOG.debug("AddRole's count:" + count);
		return new ResponseEntity(StatusCode.SUCCESS, "成功插入" + count + "条记录", role.getId());
	}

	@Override
	public ResponseEntity getAllRoles() {
		Role[] roles = rolesMapper.selectAllRoles();
		return new ResponseEntity(StatusCode.SUCCESS, "", roles);
	}

	@Override
	public ResponseEntity grantPermissionForRole(String roleId, String permissions) {
		String[] permissionIds = permissions.split(",");
		int count = 0;

		try {
			for (String permissionId : permissionIds) {
				rolePermissionsMapper.insert(roleId, permissionId);
				count++;
			}
		} catch (DataAccessException e) {
			e.printStackTrace();

			// 回滚
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

			return new ResponseEntity(StatusCode.ERROR);
		}

		LOG.debug("Grant permission's count:" + count);
		return new ResponseEntity(StatusCode.SUCCESS, "成功插入" + count + "条记录", count);
	}

	@Override
	public ResponseEntity getRolesForUser(String userId) {
		String rolesString = "";
		Role[] roles = rolesMapper.selectRolesByUserId(userId);
		for (Role role : roles) {
			rolesString += role.getRoleName() + ",";
		}

		return new ResponseEntity(StatusCode.SUCCESS, "", rolesString);
	}

}
