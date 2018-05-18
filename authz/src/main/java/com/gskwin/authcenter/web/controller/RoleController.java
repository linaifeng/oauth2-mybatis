package com.gskwin.authcenter.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.common.StatusCode;
import com.gskwin.authcenter.domain.oauth.Role;
import com.gskwin.authcenter.domain.shared.BeanProvider;
import com.gskwin.authcenter.service.RoleService;

@Controller
@RequestMapping("role/")
public class RoleController {

	private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);
	private transient RoleService roleService = BeanProvider.getBean(RoleService.class);

	/**
	 * 增加角色
	 * 
	 * @param role
	 * @param permission
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "addRole", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addRole(@ModelAttribute("role") Role role, HttpServletRequest request) {
		if (role.getRoleName() == null) {
			LOG.debug("Request has no roleName");
			return new ResponseEntity(StatusCode.INVALID_REQUEST, "Request has no roleName");
		}

		ResponseEntity responseEntity = roleService.addRole(role);

		return responseEntity;
	}

	/**
	 * 获取所有的角色
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getAllRoles", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity getAllRoles(HttpServletRequest request) {
		ResponseEntity responseEntity = roleService.getAllRoles();

		return responseEntity;
	}

	/**
	 * 为角色设置权限
	 * 
	 * @param roleId
	 * @param permissions
	 *            用【,】隔开的字符串
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "grantPermissionsForRole", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity grantPermissionForRole(String roleId, String permissions, HttpServletRequest request) {

		ResponseEntity responseEntity = roleService.grantPermissionForRole(roleId, permissions);

		return responseEntity;
	}

	/**
	 * 获取用户角色
	 * 
	 * @param userId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getRolesForUser", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity getRolesForUser(String userId, HttpServletRequest request) {
		ResponseEntity responseEntity = roleService.getRolesForUser(userId);

		return responseEntity;
	}

}
