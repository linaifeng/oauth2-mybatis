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

import com.gskwin.authcenter.common.HttpUtil;
import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.common.StatusCode;
import com.gskwin.authcenter.domain.oauth.AccessToken;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.domain.oauth.User;
import com.gskwin.authcenter.domain.shared.BeanProvider;
import com.gskwin.authcenter.infrastructure.AuthenticationException;
import com.gskwin.authcenter.service.OauthService;
import com.gskwin.authcenter.service.UserService;
import com.gskwin.authcenter.web.ValidateUtils;
import com.gskwin.authcenter.web.WebUtils;

@Controller
@RequestMapping("user/")
public class UserController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
	private transient OauthService oauthService = BeanProvider.getBean(OauthService.class);
	private transient UserService userService = BeanProvider.getBean(UserService.class);

	/**
	 * 增加用户
	 * 
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "addUser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addUser(@ModelAttribute("user") User user, HttpServletRequest request) {
		LOG.info("addUser start.");

		if (user.getUsername() == null) {
			LOG.debug("Request has no username");
			return new ResponseEntity(StatusCode.INVALID_REQUEST, "Request has no username");
		}
		if (user.getPassword() == null) {
			LOG.debug("Request has no password");
			return new ResponseEntity(StatusCode.INVALID_REQUEST, "Request has no password");
		}

		ResponseEntity responseEntity = userService.addUser(user);
		LOG.info("addUser end.");
		return responseEntity;
	}

	/**
	 * 为用户设置角色
	 * 
	 * @param userId
	 * @param roles
	 *            用【,】隔开的字符串
	 * @return
	 */
	@RequestMapping(value = "grantRoleForUser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity grantRoleForUser(String userId, String roles) {
		ResponseEntity responseEntity = userService.grantRoleForUser(userId, roles);

		return responseEntity;
	}

	/**
	 * 根据token获取用户信息
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value = "getUserInfoByTokenId", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity getUserInfoByTokenId(HttpServletRequest request, HttpServletResponse response) {
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

		User user = userService.getUserInfoByTokenId(tokenId);

		return new ResponseEntity(StatusCode.SUCCESS, "", user);
	}

	/**
	 * 根据username获取用户信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getUserInfoByUsername", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity getUserInfoByUsername(String username, HttpServletRequest request, HttpServletResponse response) {

		User user = userService.getUserInfoByUsername(username);

		return new ResponseEntity(StatusCode.SUCCESS, "", user);
	}

	/**
	 * 删除用户
	 * 
	 * @param userId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "deleteUser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity deleteUser(String userId, HttpServletRequest request) {

		ResponseEntity responseEntity = userService.deleteUser(userId);

		return responseEntity;
	}

	/**
	 * 修改密码
	 * 
	 * @param userId
	 * @param password
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "editPassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity editPassword(String userId, String oldPassword, String newPassword, HttpServletRequest request) {

		ResponseEntity responseEntity = userService.editPassword(userId, oldPassword, newPassword);

		return responseEntity;
	}

	/**
	 * 修改密码
	 * 暂时允许不登录直接修改密码
	 * TODO 暂时接口，以后会删掉
	 * 
	 * @param userId
	 * @param password
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "editPassword2", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity editPassword(String userId, String newPassword, HttpServletRequest request) {

		ResponseEntity responseEntity = userService.editPassword(userId, newPassword);

		return responseEntity;
	}

	/**
	 * 修改用户名
	 * TODO 验证输入的username
	 * 
	 * @param userId
	 * @param username
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "editUsername", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity editUsername(String userId, String username, HttpServletRequest request) {

		ResponseEntity responseEntity = userService.editUsername(userId, username);

		return responseEntity;
	}

	/**
	 * 
	 * 
	 * @param roleName
	 * @return
	 */
	@RequestMapping(value = "getUserForRole", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity getUserForRole(String roleName) {

		ResponseEntity responseEntity = userService.getUsersForRole(roleName);

		return responseEntity;
	}
}
