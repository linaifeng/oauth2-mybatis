package com.gskwin.authcenter.oauth.authorize;

import static com.gskwin.authcenter.domain.oauth.Constants.OAUTH_APPROVAL_VIEW;
import static com.gskwin.authcenter.domain.oauth.Constants.OAUTH_LOGIN_VIEW;
import static com.gskwin.authcenter.domain.oauth.Constants.REQUEST_PASSWORD;
import static com.gskwin.authcenter.domain.oauth.Constants.REQUEST_USERNAME;
import static com.gskwin.authcenter.domain.oauth.Constants.REQUEST_USER_OAUTH_APPROVAL;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.domain.shared.BeanProvider;
import com.gskwin.authcenter.infrastructure.dao.ClientDetailsMapper;
import com.gskwin.authcenter.oauth.OAuthAuthxRequest;
import com.gskwin.authcenter.oauth.OAuthHandler;
import com.gskwin.authcenter.oauth.validator.AbstractClientDetailsValidator;
import com.gskwin.authcenter.service.SecurityUtils;
import com.gskwin.authcenter.web.WebUtils;

public abstract class AbstractAuthorizeHandler extends OAuthHandler {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractAuthorizeHandler.class);
	private transient ClientDetailsMapper clientDetailsMapper = BeanProvider.getBean(ClientDetailsMapper.class);
	private transient SecurityUtils securityUtils = BeanProvider.getBean(SecurityUtils.class);

	protected OAuthAuthxRequest oauthRequest;
	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected boolean userFirstLogged = false;
	protected boolean userFirstApproved = false;

	public AbstractAuthorizeHandler(OAuthAuthxRequest oauthRequest, HttpServletResponse response) {
		this.oauthRequest = oauthRequest;
		this.request = oauthRequest.request();
		this.response = response;
	}

	protected boolean validateFailed() throws OAuthSystemException {
		AbstractClientDetailsValidator validator = getValidator();
		LOG.debug("Use [{}] validate client: {}", validator, oauthRequest.getClientId());

		final OAuthResponse oAuthResponse = validator.validate();
		return checkAndResponseValidateFailed(oAuthResponse);
	}

	protected abstract AbstractClientDetailsValidator getValidator();

	protected boolean checkAndResponseValidateFailed(OAuthResponse oAuthResponse) {
		if (oAuthResponse != null) {
			LOG.debug("Validate OAuthAuthzRequest(client_id={}) failed", oauthRequest.getClientId());
			WebUtils.writeOAuthJsonResponse(response, oAuthResponse);
			return true;
		}
		return false;
	}

	protected String clientId() {
		return oauthRequest.getClientId();
	}

	protected boolean isUserAuthenticated() {
		HttpSession session = request.getSession();
		return securityUtils.isAuthenticated(session);
	}

	protected boolean isNeedUserLogin() {
		return !isUserAuthenticated() && !isPost();
	}

	protected boolean goApproval() throws ServletException, IOException {
		if (userFirstLogged && !clientDetails().trusted()) {
			// go to approval
			LOG.debug("Go to oauth_approval, clientId: '{}'", clientDetails().getClientId());
			request.getRequestDispatcher(OAUTH_APPROVAL_VIEW).forward(request, response);
			return true;
		}
		return false;
	}

	// true is submit failed, otherwise return false
	protected boolean submitApproval() throws IOException, OAuthSystemException {
		if (isPost() && !clientDetails().trusted()) {
			// submit approval
			final String oauthApproval = request.getParameter(REQUEST_USER_OAUTH_APPROVAL);
			if (!"true".equalsIgnoreCase(oauthApproval)) {
				// Deny action
				LOG.debug("User '{}' deny access", securityUtils.getPrincipal(request.getSession()));
				responseApprovalDeny();
				return true;
			} else {
				userFirstApproved = true;
				return false;
			}
		}
		return false;
	}

	protected void responseApprovalDeny() throws IOException, OAuthSystemException {
		final OAuthResponse oAuthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND).setError(OAuthError.CodeResponse.ACCESS_DENIED)
				.setErrorDescription("User denied access").location(clientDetails().getRedirectUri()).setState(oauthRequest.getState()).buildQueryMessage();
		LOG.debug("'ACCESS_DENIED' response: {}", oAuthResponse);

		WebUtils.writeOAuthQueryResponse(response, oAuthResponse);

		// user logout when deny
		securityUtils.logout(request.getSession());
		LOG.debug("After 'ACCESS_DENIED' call logout. user: {}", securityUtils.getPrincipal(request.getSession()));
	}

	protected boolean goLogin() throws ServletException, IOException {
		if (isNeedUserLogin()) {
			// go to login
			LOG.debug("Forward to Oauth login by client_id '{}'", oauthRequest.getClientId());

			// 增加iconUri
			ClientDetails clientDetail = clientDetailsMapper.selectByPrimaryKey(oauthRequest.getClientId());
			request.setAttribute("clientName", clientDetail.getClientName());
			request.setAttribute("clientIconUri", clientDetail.getClientIconUri());
			request.getRequestDispatcher(OAUTH_LOGIN_VIEW).forward(request, response);
			return true;
		}
		return false;
	}

	// true is login failed, false is successful
	protected boolean submitLogin() throws ServletException, IOException {
		if (isSubmitLogin()) {
			// login flow
			try {
				securityUtils.login(request.getParameter(REQUEST_USERNAME), request.getParameter(REQUEST_PASSWORD), request.getSession());

				LOG.debug("Submit login successful");
				this.userFirstLogged = true;
				return false;

			} catch (OAuthSystemException e) {
				LOG.error("SecurityUtils.md5GenerateValue() throws exception.");
				return true;

			} catch (Exception ex) {
				// login failed
				LOG.debug("Login failed, back to login page too", ex);

				// 增加iconUri
				ClientDetails clientDetail = clientDetailsMapper.selectByPrimaryKey(oauthRequest.getClientId());
				request.setAttribute("clientName", clientDetail.getClientName());
				request.setAttribute("clientIconUri", clientDetail.getClientIconUri());
				request.setAttribute("oauth_login_error", true);
				request.getRequestDispatcher(OAUTH_LOGIN_VIEW).forward(request, response);
				return true;

			}
		}
		return false;
	}

	private boolean isSubmitLogin() {
		return !isUserAuthenticated() && isPost();
	}

	protected boolean isPost() {
		return RequestMethod.POST.name().equalsIgnoreCase(request.getMethod());
	}

	public void handle() throws OAuthSystemException, ServletException, IOException {
		// validate
		if (validateFailed()) {
			return;
		}

		// Check need usr login
		if (goLogin()) {
			return;
		}

		// submit login
		if (submitLogin()) {
			return;
		}

		// Check approval
		if (goApproval()) {
			return;
		}

		// Submit approval
		if (submitApproval()) {
			return;
		}

		// handle response
		handleResponse();
	}

	// Handle custom response content
	protected abstract void handleResponse() throws OAuthSystemException, IOException;
}
