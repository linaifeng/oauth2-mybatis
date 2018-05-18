package com.gskwin.authcenter.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.gskwin.authcenter.common.HttpUtil;
import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.common.StatusCode;
import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.domain.oauth.Permission;
import com.gskwin.authcenter.domain.shared.BeanProvider;
import com.gskwin.authcenter.infrastructure.AuthenticationException;
import com.gskwin.authcenter.service.OauthService;
import com.gskwin.authcenter.service.PermissionService;
import com.gskwin.authcenter.web.ValidateUtils;
import com.gskwin.authcenter.web.WebUtils;

@Controller
@RequestMapping("permission/")
public class PermissionController {

	private static final Logger LOG = LoggerFactory.getLogger(PermissionController.class);
	private transient OauthService oauthService = BeanProvider.getBean(OauthService.class);
	private transient PermissionService permissionService = BeanProvider.getBean(PermissionService.class);

	/**
	 * 增加权限
	 * 
	 * @param permission
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "addPermission", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addPermission(@ModelAttribute("permission") Permission permission, HttpServletRequest request) {
		if (permission.getPermissionName() == null) {
			LOG.debug("Request has no permissionName");
			return new ResponseEntity(StatusCode.INVALID_REQUEST, "Request has no permissionName");
		}

		ResponseEntity responseEntity = permissionService.addPermission(permission);

		return responseEntity;
	}

	/**
	 * 获取所有权限
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getAllPermissions", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity getAllPermissions(HttpServletRequest request) {

		ResponseEntity responseEntity = permissionService.getAllPermissions();

		return responseEntity;
	}

	/**
	 * 获取用户权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value = "getPermissionsForUser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity getPermissionsForUser(HttpServletRequest request, HttpServletResponse response) {
		String tokenId = HttpUtil.getParameter(request, OAuth.OAUTH_ACCESS_TOKEN);
		AccessToken accessToken = null;
		try {
			// 验证传入的access_token
			accessToken = oauthService.loadAccessTokenByTokenId(tokenId);
			ValidateUtils.validateToken(tokenId, accessToken);

			// 验证 clientDetails
			final ClientDetails clientDetails = oauthService.loadClientDetails(accessToken.clientId());
			ValidateUtils.validateClientDetails(tokenId, accessToken, clientDetails);
		} catch (AuthenticationException e) {
			OAuthResponse oAuthResponse = null;
			try {
				oAuthResponse = OAuthResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED).setError(e.getError()).setErrorDescription(e.getMessage()).buildJSONMessage();
			} catch (OAuthSystemException e1) {
				e1.printStackTrace();
			} finally {
				if (oAuthResponse != null) {
					WebUtils.writeOAuthJsonResponse((HttpServletResponse) response, oAuthResponse);
				}
				return null;
			}
		}

		Permission[] permissions = permissionService.getPermissionsByUsername(accessToken.getUsername());

		JSONArray permissionJsonArray = new JSONArray();
		for (Permission permission : permissions) {
			permissionJsonArray.add(permission.getPermissionName());
		}

		String permissionsString = permissionJsonArray.toJSONString();
		return new ResponseEntity(StatusCode.SUCCESS, "", permissionsString);
	}
}
