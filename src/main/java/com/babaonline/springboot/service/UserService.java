package com.babaonline.springboot.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.babaonline.springboot.model.User;
import com.babaonline.springboot.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService{
	User save(UserRegistrationDto registrationDto);
}
