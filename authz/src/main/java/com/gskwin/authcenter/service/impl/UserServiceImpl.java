package com.gskwin.authcenter.service.impl;

import java.util.Date;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.common.StatusCode;
import com.gskwin.authcenter.domain.oauth.Role;
import com.gskwin.authcenter.domain.oauth.User;
import com.gskwin.authcenter.domain.shared.GuidGenerator;
import com.gskwin.authcenter.infrastructure.dao.RolesMapper;
import com.gskwin.authcenter.infrastructure.dao.UserRolesMapper;
import com.gskwin.authcenter.infrastructure.dao.UsersMapper;
import com.gskwin.authcenter.service.SecurityUtils;
import com.gskwin.authcenter.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
	// @Autowired
	// private DefaultHashService hashService;
	@Autowired
	private UsersMapper usersMapper;
	@Autowired
	private UserRolesMapper userRolesMapper;
	@Autowired
	private RolesMapper rolesMapper;

	@Override
	public ResponseEntity addUser(User user) {
		// 验证username唯一性
		User userDb = usersMapper.selectByUsername(user.getUsername());
		if (userDb != null) {
			// 如果用户已删除（已有此用户且archived=1）则更新archived=0
			if (userDb.getArchived() == true) {
				userDb.setArchived(false);
				//重置密码
				try {
					userDb.setPassword(SecurityUtils.md5GenerateValue(user.getPassword()));
				} catch (OAuthSystemException e) {
					e.printStackTrace();

					return new ResponseEntity(StatusCode.ERROR);
				}  
				
				
				int count = usersMapper.updateByPrimaryKeySelective(userDb);

				LOG.debug("update user's count:" + count);
				return new ResponseEntity(StatusCode.SUCCESS,"成功更新" + count + "条记录",userDb.getId());
			} else {
				return new ResponseEntity(StatusCode.INVALID_REQUEST, "已经有此用户名");
			}
		}

		user.setId(GuidGenerator.generate());
		user.setCreateTime(new Date());
		try {
			user.setPassword(SecurityUtils.md5GenerateValue(user.getPassword()));
		} catch (OAuthSystemException e) {
			e.printStackTrace();

			return new ResponseEntity(StatusCode.ERROR);
		}

		int count = 0;
		try {
			count = usersMapper.insertSelective(user);
		} catch (DataAccessException e) {
			e.printStackTrace();

			return new ResponseEntity(StatusCode.ERROR);
		}
		LOG.debug("Add user's count:" + count);
		return new ResponseEntity(StatusCode.SUCCESS, "成功插入" + count + "条记录", user.getId());
	}

	/**
	 * 保证roles中的角色是数据库中的角色
	 * roles如果是空字符串，代表删除所有权限
	 * 
	 */
	@Override
	public ResponseEntity grantRoleForUser(String userId, String roles) {
		// 1.删除用户的所有角色
		int deleteCount = userRolesMapper.deleteByUserId(userId);
		LOG.debug("Delete role's count:" + deleteCount);

		// 2.插入用户的角色
		int insertCount = 0;
		if (!"".equals(roles)) {
			String[] roleNames = roles.split(",");

			Role[] allRoles = rolesMapper.selectAllRoles();

			try {
				for (String roleName : roleNames) {
					boolean flag = false;

					Role role = null;
					for (Role tmpRole : allRoles) {
						if (roleName.equals(tmpRole.getRoleName())) {
							flag = true;
							role = tmpRole;
							break;
						}
					}
					if (flag == true) {
						userRolesMapper.insert(userId, role.getId());
						insertCount++;
					} else {
						// 回滚
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						return new ResponseEntity(StatusCode.INVALID_REQUEST, "请检查参数");
					}
				}
			} catch (DataAccessException e) {
				e.printStackTrace();
				// 回滚
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return new ResponseEntity(StatusCode.ERROR);
			}
		}
		LOG.debug("Grant role's count:" + insertCount);
		return new ResponseEntity(StatusCode.SUCCESS, "成功插入" + insertCount + "条记录", insertCount);
	}

	@Override
	public User getUserInfoByTokenId(String tokenId) {
		return usersMapper.getUserInfoByTokenId(tokenId);
	}

	@Override
	public User getUserInfoByUsername(String username) {
		return usersMapper.selectByUsername(username);
	}

	@Override
	public ResponseEntity deleteUser(String userId) {
		User user = new User();
		user.setId(userId);
		user.setArchived(true);

		// 删除user，更新为archived
		int count = usersMapper.updateByPrimaryKeySelective(user);
		if (count == 1) {
			// 删除user_role
			userRolesMapper.deleteByUserId(userId);

			return new ResponseEntity(StatusCode.SUCCESS);
		}

		// 回滚
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return new ResponseEntity(StatusCode.INVALID_REQUEST, "请检测userId");
	}

	@Override
	public ResponseEntity editPassword(String userId, String oldPassword, String newPassword) {
		String password = usersMapper.selectPasswordByPrimaryKey(userId);

		try {
			if (password != null && password.equals(SecurityUtils.md5GenerateValue(oldPassword))) {
				if (oldPassword.equals(newPassword)) {
					return new ResponseEntity(StatusCode.INVALID_REQUEST, "新旧密码不能相同");
				}

				int count = usersMapper.editPassword(userId, SecurityUtils.md5GenerateValue(newPassword));
				if (count == 1) {
					return new ResponseEntity(StatusCode.SUCCESS);
				}
			} else if (password != null && !password.equals(SecurityUtils.md5GenerateValue(oldPassword))) {
				return new ResponseEntity(StatusCode.INVALID_REQUEST, "原始密码不正确");
			}
		} catch (OAuthSystemException e) {
			LOG.error("SecurityUtils.md5GenerateValue() throws exception.");
			return new ResponseEntity(StatusCode.ERROR, "内部错误");
		}

		return new ResponseEntity(StatusCode.INVALID_REQUEST, "请检测userId");
	}

	@Override
	public ResponseEntity editPassword(String userId, String newPassword) {
		String password = usersMapper.selectPasswordByPrimaryKey(userId);

		try {
			if (password != null) {
				int count = usersMapper.editPassword(userId, SecurityUtils.md5GenerateValue(newPassword));
				if (count == 1) {
					return new ResponseEntity(StatusCode.SUCCESS);
				}
			}
		} catch (OAuthSystemException e) {
			LOG.error("SecurityUtils.md5GenerateValue() throws exception.");
			return new ResponseEntity(StatusCode.ERROR, "内部错误");
		}

		return new ResponseEntity(StatusCode.INVALID_REQUEST, "请检测userId");
	}

	@Override
	public ResponseEntity editUsername(String userId, String username) {
		// username不能重复并且不能和当前username相同
		User userDb = usersMapper.selectByUsername(username);
		if (userDb != null) {
			if (userId.equals(userDb.getId())) {
				return new ResponseEntity(StatusCode.INVALID_REQUEST, "请更改username");
			}

			return new ResponseEntity(StatusCode.INVALID_REQUEST, "此username已存在");
		}

		int count = usersMapper.editUsername(userId, username);
		if (count == 1) {
			return new ResponseEntity(StatusCode.SUCCESS);
		}

		// 回滚
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return new ResponseEntity(StatusCode.INVALID_REQUEST, "请检测userId");
	}

	@Override
	public ResponseEntity getUsersForRole(String roleName) {

		User[] users = usersMapper.getUsersForRole(roleName);

		return new ResponseEntity(StatusCode.SUCCESS, "", users);
	}

	@Override
	public int updateByPrimaryKeySelective(User user) {
		return usersMapper.updateByPrimaryKeySelective(user);
	}
}
