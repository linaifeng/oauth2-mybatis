package com.gskwin.authcenter.web.context;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.CharacterEncodingFilter;

import com.gskwin.authcenter.infrastructure.ThreadLocalHolder;
import com.gskwin.authcenter.web.WebUtils;

public class GskCharacterEncodingFilter extends CharacterEncodingFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		persistIp(request);
		super.doFilterInternal(request, response, filterChain);
	}

	private void persistIp(HttpServletRequest request) {
		final String clientIp = WebUtils.retrieveClientIp(request);
		ThreadLocalHolder.clientIp(clientIp);
	}

}