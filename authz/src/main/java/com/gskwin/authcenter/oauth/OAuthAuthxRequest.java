package com.gskwin.authcenter.oauth;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

public class OAuthAuthxRequest extends OAuthAuthzRequest {

	public OAuthAuthxRequest(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
		super(request);
	}

	public boolean isCode() {
		return ResponseType.CODE.name().equalsIgnoreCase(this.getResponseType());
	}

	public boolean isToken() {
		return ResponseType.TOKEN.name().equalsIgnoreCase(this.getResponseType());
	}

	public HttpServletRequest request() {
		return this.request;
	}
}
