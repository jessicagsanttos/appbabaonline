package com.babaonline.springboot.web;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.babaonline.springboot.model.User;
import com.babaonline.springboot.model.Utility;
import com.babaonline.springboot.service.UserService;

import net.bytebuddy.utility.RandomString;


@Controller
public class ForgotPasswordController {
	
	private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

	@Autowired
    private JavaMailSender mailSender;
     
    @Autowired
    private UserService userService;
     
    @GetMapping("/forgotPasswordForm")
    public String forgot() {
    	logger.info("showForgotPasswordForm");
        return "forgot";
    }
 
    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        logger.info("processForgotPassword " + email );
        
        String token = RandomString.make(30);
         
        try {
        	userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
             
        }catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }
        catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        } 
             
        return "forgot";
    }
    
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User customer = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);
         
        if (customer == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        }
         
        return "forgot_password_form";
    }
     
    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
    	logger.info("sendEmail");
    	MimeMessage message = mailSender.createMimeMessage();              
        MimeMessageHelper helper = new MimeMessageHelper(message);
         
        helper.setFrom("naoresponda@babaonline.com", "Baba Online");
        helper.setTo(recipientEmail);
         
        String subject = "Here's the link to reset your password";
         
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";
         
        helper.setSubject(subject);
         
        helper.setText(content, true);
         
        mailSender.send(message); 
    } 
    
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "login";
    }
     
    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
         
        User customer = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");
         
        if (customer == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        } else {           
        	userService.updatePassword(customer, password);
             
            model.addAttribute("message", "You have successfully changed your password.");
        }
         
        return "password_msg";
    }
    
    
}