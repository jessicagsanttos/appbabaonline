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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserService userService;

	Logger logger = LogManager.getLogger(CustomLoginFailureHandler.class);

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		logger.debug("Entrando em onAuthenticationFailure");

		String email = request.getParameter("username");
		User user = userService.getByEmail(email);
		String msg = "";

		if (user != null) {
			logger.debug("Usuario carregado a partir do seu username");
			validarConta(user, exception, msg);
		} else {
			logger.info("Usuario não encontrado a partir do seu username");
		}

		super.setDefaultFailureUrl("/login?error");
		super.onAuthenticationFailure(request, response, exception);
	}

	private void validarConta(User user, AuthenticationException exception, String msg) {
		logger.debug("Iniciando validação da conta do usuário" + user.getEmail());

		boolean contaBloqueada = user.isAccountNonLocked() == 1;

		if (contaBloqueada) {
			logger.debug("Usuário habilitado e conta ativa");
			
			boolean loginsInvalidosMenorQueLimite = user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1;
			
			tratarFalhaLogin(loginsInvalidosMenorQueLimite, exception, user, msg);
			
		} else{
			avaliarLiberacaoConta(user, msg, exception);
		}

	}

	private void avaliarLiberacaoConta(User user, String msg, AuthenticationException exception) {
		if (userService.unlockWhenTimeExpired(user)) {
			logger.debug("Usuário ha mais de 24 horas trancado, conta liberada");
			exception = new LockedException("Your account has been unlocked. Please try to login again.");
			msg = "Your account has been unlocked. Please try to login again.";

		}else {
			logger.debug("Conta trancada por excesso de falhas de logins. Tente novamente após 24 horas");
			msg = "Your account has been locked due to 3 failed attempts.\"\r\n"
					+ "                            + \" It will be unlocked after 24 hours.\"";
			
			
		}
		
	}

	private void tratarFalhaLogin(boolean loginsInvalidosMenorQueLimite, AuthenticationException exception, User user, String msg) {
		if (loginsInvalidosMenorQueLimite) {
			logger.debug("Incrementando histórico de logins invalidos em sequência");
			userService.increaseFailedAttempts(user);
		} else {
			
			userService.increaseFailedAttempts(user);
			userService.lock(user);
			exception = new LockedException("Your account has been locked due to 3 failed attempts."
					+ " It will be unlocked after 24 hours.");
			
			msg = "Your account has been locked due to 3 failed attempts.\"\r\n"
					+ "                            + \" It will be unlocked after 24 hours.\"";
		}
		
	}

}
