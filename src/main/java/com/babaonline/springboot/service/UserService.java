package com.babaonline.springboot.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.babaonline.springboot.model.User;
import com.babaonline.springboot.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService{
	
	public static final int MAX_FAILED_ATTEMPTS = 3;

	User save(UserRegistrationDto registrationDto);

	void increaseFailedAttempts(User user);

	boolean unlockWhenTimeExpired(User user);
	
	public void lock(User user);

	User getByEmail(String email);

	void resetFailedAttempts(String email);

	void updateResetPasswordToken(String token, String email);

}
