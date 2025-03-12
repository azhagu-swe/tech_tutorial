package com.azhag_swe.tech_tutorial.controller;

import com.azhag_swe.tech_tutorial.dto.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * For Swagger login controller.
 */
@Controller
public class SwaggerLoginController {

	@Value("${swagger.username:admin}")
	private String username;

	@Value("${swagger.password:admin}")
	private String pword;

	@GetMapping(value = "/")
	public String login() {
		// Redirect to the custom login page
		return "redirect:/login.html";
	}

	@ResponseBody
	@PostMapping(value = "/login")
	public Boolean loginSwagger(@RequestBody LoginRequest dto, HttpServletRequest req) {
		Boolean isLoggedInSuccess = false;
		String userName = dto.getUsername();
		String password = dto.getPassword();

		// Check if provided credentials match the static ones.
		if (this.username.equals(userName) && this.pword.equals(password)) {
			// Create an authentication token with a default authority
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					userName,
					null,
					List.of(new SimpleGrantedAuthority("Admin")));

			// Set the token in the security context so that Spring Security knows the user
			// is authenticated.
			SecurityContextHolder.getContext().setAuthentication(authToken);

			// Also create an HTTP session (optional, if you need session for other
			// purposes)
			HttpSession session = req.getSession();
			session.setMaxInactiveInterval(2 * 60 * 60); // 2 hours session
			session.setAttribute("swagger-user", userName);
			isLoggedInSuccess = true;
		}
		return isLoggedInSuccess;
	}
}
