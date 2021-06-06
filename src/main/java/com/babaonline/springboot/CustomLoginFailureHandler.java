package com.babaonline.springboot;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.babaonline.springboot.model.User;
import com.babaonline.springboot.service.UserService;
 
@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
     
    @Autowired
    private UserService userService;
     
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
      
    	System.out.println("Entrando em onAuthenticationFailure");
    	
    	System.out.println(request.getParameterMap());
    	
    	String email = request.getParameter("username");
        User user = userService.getByEmail(email);
        String msg = "";
        
        
        if (user != null) {
        	System.out.println("Usuario " + user.isEnabled() + "- " + user.isAccountNonLocked());
        	if (user.isEnabled() && user.isAccountNonLocked() == 1) {
        		System.out.println("user.getFailedAttempt() " + user.getFailedAttempt());
                if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1) {
                    userService.increaseFailedAttempts(user);
                } else {
                	System.out.println("Trancar usuario " + user);
                    userService.lock(user);
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 24 hours.");
                    msg = "Your account has been locked due to 3 failed attempts.\"\r\n"
                    		+ "                            + \" It will be unlocked after 24 hours.\"";
                
                }
            } else if (user.isAccountNonLocked()!= 1) {
                if (userService.unlockWhenTimeExpired(user)) {
                	System.out.println("Destrancar usuario " + user);
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                     msg = "Your account has been unlocked. Please try to login again.";
                
                }
                 msg = "Your account has been locked due to 3 failed attempts.\"\r\n"
                		+ "                            + \" It will be unlocked after 24 hours.\"";
            }
             
        }
        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
 
}
