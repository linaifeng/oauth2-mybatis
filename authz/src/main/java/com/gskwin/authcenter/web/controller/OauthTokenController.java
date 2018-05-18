package com.gskwin.authcenter.web.controller;

import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gskwin.authcenter.oauth.OAuthTokenxRequest;
import com.gskwin.authcenter.oauth.token.OAuthTokenHandleDispatcher;
import com.gskwin.authcenter.web.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("oauth/")
public class OauthTokenController {

	/**
	 * Handle grant_types as follows:
	 * <p/>
	 * grant_type=authorization_code
	 * grant_type=password
	 * grant_type=refresh_token
	 * grant_type=client_credentials
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws Exception
	 */
	@RequestMapping(value = "token")
	public void authorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			OAuthTokenxRequest tokenRequest = new OAuthTokenxRequest(request);

			OAuthTokenHandleDispatcher tokenHandleDispatcher = new OAuthTokenHandleDispatcher(tokenRequest, response);
			tokenHandleDispatcher.dispatch();

		} catch (OAuthProblemException e) {
			//exception
			OAuthResponse oAuthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND).location(e.getRedirectUri()).error(e).buildJSONMessage();
			WebUtils.writeOAuthJsonResponse(response, oAuthResponse);
		}

	}
}
