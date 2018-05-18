package com.gskwin.authcenter.oauth.token;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import com.gskwin.authcenter.oauth.OAuthTokenxRequest;


public interface OAuthTokenHandler {

	boolean support(OAuthTokenxRequest tokenRequest) throws OAuthProblemException;

	void handle(OAuthTokenxRequest tokenRequest, HttpServletResponse response) throws OAuthProblemException, OAuthSystemException;

}