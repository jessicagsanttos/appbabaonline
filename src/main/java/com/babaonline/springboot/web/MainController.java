package com.babaonline.springboot.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@GetMapping("/login")
	public String login() {
		logger.info("Controller, metodo login");
		return "login_form";
	}
	
	@GetMapping("/loginInvalido")
	public String loginInvalido() {
		logger.info("Controller, metodo login invalido");
		return "login_form";
	}
	
	@GetMapping("/")
	public String home() {
		return "index";
	}
}
