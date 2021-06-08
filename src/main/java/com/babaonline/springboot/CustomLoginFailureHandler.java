package com.babaonline.springboot;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(CustomLoginFailureHandler.class);

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		logger.info("Entrando em onAuthenticationFailure");

		String email = request.getParameter("username");
		User user = userService.getByEmail(email);
		String codigoErro = "";

		if (user != null) {
			logger.info("Usuario carregado a partir do seu username");
			 codigoErro = validarConta(user, userService, exception);
		} else {
			logger.info("Usuario não encontrado a partir do seu username");
		}
		
		logger.info("Finalizando o metodo onAuthenticationFailure: " + "/login?error" + codigoErro);
		super.setDefaultFailureUrl("/login?error" + codigoErro);
		super.onAuthenticationFailure(request, response, exception);
	}

	private String validarConta(User user, UserService userService2, AuthenticationException exception) {
		logger.info("Iniciando validação da conta do usuário" + user.getEmail());

		boolean usuarioValido = user.isAccountNonLocked() == 0;

		if (usuarioValido) {
			logger.info("Usuário habilitado e conta ativa");
			
			logger.info("Numero de tentativas: " + user.getFailedAttempt());
			
			boolean loginsInvalidosMenorQueLimite = user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1;

			if (loginsInvalidosMenorQueLimite) {
				logger.info("Incrementando histórico de logins invalidos em sequência");
				userService.increaseFailedAttempts(user);
			} else {

				userService.increaseFailedAttempts(user);
				userService.lock(user);
				exception = new LockedException("Your account has been locked due to 3 failed attempts."
						+ " It will be unlocked after 24 hours.");
			

			}
			
			return "";
		} else {
			logger.info("Removendo lógica de liberar conta após 24 horas");
			return "2";
			/*if (userService.unlockWhenTimeExpired(user)) {
				logger.info("Usuário ha mais de 24 horas trancado, conta liberada");
				exception = new LockedException("Your account has been unlocked. Please try to login again.");

			} else {
				logger.info("Usuário há menos de 24 horas trancado, tente novamente");
				url = "4";

			}*/

		}

	}

}
