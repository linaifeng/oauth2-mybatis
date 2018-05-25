package com.gskwin.authcenter.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

	@RequestMapping(value = "/main")
	public ModelAndView goMainPage() throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("main/main");
		return mv;
	}

	@RequestMapping(value = "/test_client")
	public ModelAndView goTest(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("main/test");
		return mv;
	}

	@RequestMapping(value = "/auth_api1")
	public ModelAndView goApi1(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("apidoc/autz_01");
		return mv;
	}

}
