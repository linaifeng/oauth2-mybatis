package com.gskwin.authcenter.service;

import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gskwin.authcenter.domain.oauth.User;
import com.gskwin.authcenter.infrastructure.AuthenticationException;
import com.gskwin.authcenter.infrastructure.dao.UsersMapper;

@Service("securityUtils")
public class SecurityUtils {
	private static final Logger LOG = LoggerFactory.getLogger(SecurityUtils.class);
	private static final String USERNAME = "USERNAME";
	private static final String AUTHENTICATED = "AUTHENTICATED";

	@Autowired
	private UsersMapper userMapper;

	public void login(String username, String password, HttpSession session) throws OAuthSystemException {
		User user = userMapper.selectByUsername(username);

		if (user == null) {
			LOG.error("User:{} not found.", username);
			throw new AuthenticationException("User:{ " + username + "} not found");
		}

		String passwordDb = userMapper.selectPasswordByPrimaryKey(user.getId());

		if (md5GenerateValue(password).equals(passwordDb)) {
			session.setAttribute(USERNAME, username);
			session.setAttribute(AUTHENTICATED, true);
			LOG.debug("Login sucess, add session.");
		} else {
			LOG.error("User:" + username + "'s password does not match .");
			throw new AuthenticationException("User:" + username + "'s password does not match .");
		}
	}

	public void logout(HttpSession session) {
		session.removeAttribute(USERNAME);
		session.removeAttribute(AUTHENTICATED);

		LOG.debug("Logout and remove session.");
	}

	public String getPrincipal(HttpSession session) {
		return (null == session.getAttribute(USERNAME)) ? "" : (String) session.getAttribute(USERNAME);
	}

	public boolean isAuthenticated(HttpSession session) {
		return (null == session.getAttribute(AUTHENTICATED)) ? false : (boolean) session.getAttribute(AUTHENTICATED);
	}

	public static String md5GenerateValue(String password) throws OAuthSystemException {
		return new MD5Generator().generateValue(password);
	}
}
