package com.babaonline.admin.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.babaonline.springboot.model.User;
import com.babaonline.springboot.service.ShopmeUserDetails;
import com.babaonline.springboot.service.UserService;
 
@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
 
    @Autowired
    private UserService userService;
     
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        ShopmeUserDetails userDetails =  (ShopmeUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user.getEmail());
        }
         
        super.onAuthenticationSuccess(request, response, authentication);
    }
     
}