package com.babaonline.springboot.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.babaonline.springboot.CustomLoginFailureHandler;
import com.babaonline.springboot.model.Role;
import com.babaonline.springboot.model.User;
import com.babaonline.springboot.repository.UserRepository;
import com.babaonline.springboot.web.dto.UserRegistrationDto;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;

	private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(CustomLoginFailureHandler.class);

	public UserServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public User save(UserRegistrationDto registrationDto) {
		User user = new User(registrationDto.getFirstName(), registrationDto.getLastName(), registrationDto.getEmail(),
				passwordEncoder.encode(registrationDto.getPassword()), Arrays.asList(new Role("ROLE_USER")));

		return userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		logger.info("Entrando metodo loadUserByUsername: " + username);

		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}

		if (user.isAccountNonLocked() == 1) {
			logger.info("Conta bloqueada: " + username);
			throw new UsernameNotFoundException("Usuario Bloqueado.");
		}

		logger.info("Usuario carregado com sucesso.");

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Transactional
	public void increaseFailedAttempts(User user) {
		int newFailAttempts = user.getFailedAttempt() + 1;
		userRepository.updateFailedAttempts(newFailAttempts, user.getEmail());
	}

	@Transactional
	public void resetFailedAttempts(String email) {
		logger.info("Entrando resetFailedAttempts: " + email);
		userRepository.updateFailedAttempts(0, email);
	}

	@Transactional
	public void lock(User user) {
		user.setAccountNonLocked(1);
		user.setLockTime(new Date());

		userRepository.save(user);
	}

	public boolean unlockWhenTimeExpired(User user) {
		long lockTimeInMillis = user.getLockTime().getTime();
		long currentTimeInMillis = System.currentTimeMillis();

		if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
			user.setAccountNonLocked(0);
			user.setLockTime(null);
			user.setFailedAttempt(0);

			userRepository.save(user);

			return true;
		}

		return false;
	}

	@Override
	public User getByEmail(String email) {
		User user = userRepository.findByEmail(email);
		return user;
	}

}
