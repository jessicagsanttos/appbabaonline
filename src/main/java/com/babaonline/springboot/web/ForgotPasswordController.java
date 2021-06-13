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
    public String forgotPasswordForm() {
    	logger.info("showForgotPasswordForm");
        return "informe_email_form";
    }
 
    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        logger.info("esqueceu a senha " + email );
        
        String token = RandomString.make(30);
         
        try {
        	userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", " Um link para trocar a senha foi enviado para o seu e-mail.");
             
        }catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Ocorreu um erro enquanto enviamos o seu e-mail.");
        }
        catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        } 
             
        return "informe_email_form";
    }
    
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User customer = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);
         
        if (customer == null) {
            model.addAttribute("message", "Token Inválido");
            return "password_msg";
        }
         
        return "atualizar_senha_form";
    }
     
    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
    	logger.info("sendEmail");
    	MimeMessage message = mailSender.createMimeMessage();              
        MimeMessageHelper helper = new MimeMessageHelper(message);
         
        helper.setFrom("naoresponda@babaonline.com", "Baba Online");
        helper.setTo(recipientEmail);
         
        String subject = "Aqui está o link para alterar a sua senha.";
         
        String content = "<p>Olá! :) ,</p>"
                + "<p>Você solicitou alteração de senha.</p>"
                + "<p>Clique no link abaixo para fazer a alteração:</p>"
                + "<p><a href=\"" + link + "\">Alterar a senha</a></p>"
                + "<br>"
                + "<p> Ignore esse e-mail se você lembrou a sua senha, "
                + "ou você não fez a sua solicitação.</p>";
         
        helper.setSubject(subject);
         
        helper.setText(content, true);
         
        mailSender.send(message); 
    } 
    
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "login_form";
    }
     
    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
         
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Crie sua nova senha");
         
        if (user == null) {
            model.addAttribute("message", "Token Inválido");
            return "password_msg";
        } else {  
        	userService.updatePassword(user, password);
            model.addAttribute("message", "Você alterou a sua senha com sucesso. :)");
        }
         
        return "password_msg";
    }
    
    
}