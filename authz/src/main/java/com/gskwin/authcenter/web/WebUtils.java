package com.gskwin.authcenter.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.gskwin.authcenter.common.ResponseEntity;
import com.gskwin.authcenter.common.StatusCode;

public abstract class WebUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebUtils.class);

	private WebUtils() {
	}

	public static void writeOAuthJsonResponse(HttpServletResponse response, OAuthResponse oAuthResponse) {

		final int responseStatus = oAuthResponse.getResponseStatus();
		LOGGER.debug("responseStatus: {}", responseStatus);

		try {

			final Map<String, String> headers = oAuthResponse.getHeaders();
			for (String key : headers.keySet()) {
				response.addHeader(key, headers.get(key));
			}
			// CORS setting
			response.setHeader("Access-Control-Allow-Origin", "*");

			response.setContentType(OAuth.ContentType.JSON + ";charset=utf-8"); // json
			response.setStatus(responseStatus);

			// 成功将返回信息放入ResponseEntity的data
			// 失败将error对应的code放入ResponseEntity的statusCode、errorDescription放入ResponseEntity的msg
			ResponseEntity responseEntity = null;
			if (responseStatus == HttpStatus.SC_OK) {
				responseEntity = new ResponseEntity(StatusCode.SUCCESS, "", oAuthResponse.getBody());
			} else {
				String errorBody = oAuthResponse.getBody();
				JSONObject errorJson = JSONObject.parseObject(errorBody);
				String error = "" + errorJson.get(OAuthError.OAUTH_ERROR);
				String errorDescription = "" + errorJson.get(OAuthError.OAUTH_ERROR_DESCRIPTION);
				LOGGER.debug("error: {}, errorDescription: {}", error, errorDescription);

				LOGGER.debug(StatusCode.SUCCESS.toString());
				StatusCode statusCode = StatusCode.codeDescriptionMap.get(error);
				if (statusCode != null) {
					LOGGER.debug("statusCode: {}", statusCode);
					responseEntity = new ResponseEntity(statusCode, errorDescription);
				} else {
					LOGGER.error("unkown error");
					responseEntity = new ResponseEntity(StatusCode.ERROR);
				}
			}

			final PrintWriter out = response.getWriter();
			out.print(responseEntity);
			out.flush();
		} catch (IOException e) {
			throw new IllegalStateException("Write OAuthResponse error", e);
		}
	}

	public static void writeOAuthQueryResponse(HttpServletResponse response, OAuthResponse oAuthResponse) {
		final String locationUri = oAuthResponse.getLocationUri();
		try {

			final Map<String, String> headers = oAuthResponse.getHeaders();
			for (String key : headers.keySet()) {
				response.addHeader(key, headers.get(key));
			}

			response.setStatus(oAuthResponse.getResponseStatus());
			response.sendRedirect(locationUri);

		} catch (IOException e) {
			throw new IllegalStateException("Write OAuthResponse error", e);
		}
	}

	/**
	 * Retrieve client ip address
	 *
	 * @param request HttpServletRequest
	 * @return IP
	 */
	public static String retrieveClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (isUnAvailableIp(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (isUnAvailableIp(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (isUnAvailableIp(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private static boolean isUnAvailableIp(String ip) {
		return (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip));
	}
}