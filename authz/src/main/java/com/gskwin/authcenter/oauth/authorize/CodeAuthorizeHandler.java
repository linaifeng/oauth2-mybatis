package com.gskwin.authcenter.oauth.authorize;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gskwin.authcenter.domain.oauth.ClientDetails;
import com.gskwin.authcenter.oauth.OAuthAuthxRequest;
import com.gskwin.authcenter.oauth.validator.AbstractClientDetailsValidator;
import com.gskwin.authcenter.oauth.validator.CodeClientDetailsValidator;
import com.gskwin.authcenter.web.WebUtils;

public class CodeAuthorizeHandler extends AbstractAuthorizeHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CodeAuthorizeHandler.class);

	public CodeAuthorizeHandler(OAuthAuthxRequest oauthRequest, HttpServletResponse response) {
		super(oauthRequest, response);
	}

	@Override
	protected AbstractClientDetailsValidator getValidator() {
		return new CodeClientDetailsValidator(oauthRequest);
	}

	// response code
	@Override
	protected void handleResponse() throws OAuthSystemException, IOException {
		final ClientDetails clientDetails = clientDetails();
		final String authCode = oauthService.retrieveAuthCode(clientDetails, this.currentUsername(request));

		final OAuthResponse oAuthResponse = OAuthASResponse.authorizationResponse(oauthRequest.request(), HttpServletResponse.SC_OK).location(clientDetails.getRedirectUri())
				.setCode(authCode).buildQueryMessage();
		LOG.debug(" 'code' response: {}", oAuthResponse);

		WebUtils.writeOAuthQueryResponse(response, oAuthResponse);
	}

}
