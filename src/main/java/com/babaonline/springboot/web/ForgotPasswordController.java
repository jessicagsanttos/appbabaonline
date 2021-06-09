package com.babaonline.springboot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.babaonline.springboot.service.UserService;

@Controller
public class ForgotPasswordController {
    @Autowired
    private JavaMailSender mailSender;
     
    @Autowired
    private UserService userService;
     
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "forgot_password_form";
    }
 
    @PostMapping("/forgot_password")
    public String processForgotPassword() {
    	return "forgot_password_form";
    }
     
    public void sendEmail(){
 
    }  
     
     
    @GetMapping("/reset_password")
    public String showResetPasswordForm() {
    	return "forgot_password_form";
    }
     
    @PostMapping("/reset_password")
    public String processResetPassword() {
    	return "forgot_password_form";
    }
}