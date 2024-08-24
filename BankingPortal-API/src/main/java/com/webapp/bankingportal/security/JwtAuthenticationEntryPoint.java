package com.webapp.bankingportal.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<?> handleDisabledException(DisabledException ex) {
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body("Waiting for approval");
	}

	@Override
	    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        PrintWriter writer = response.getWriter();
	        writer.println("Access Denied !! " + authException.getMessage());
	    }
  

}
