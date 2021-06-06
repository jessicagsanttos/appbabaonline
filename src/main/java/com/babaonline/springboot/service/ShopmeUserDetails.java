package com.babaonline.springboot.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.babaonline.springboot.model.User;

public class ShopmeUserDetails implements UserDetails {

	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ShopmeUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getFirstName();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return user.isEnabled();
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return user.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return user.isEnabled();
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return user.isEnabled();
	}

}
