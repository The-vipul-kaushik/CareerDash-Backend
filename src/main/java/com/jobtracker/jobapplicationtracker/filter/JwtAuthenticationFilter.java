package com.jobtracker.jobapplicationtracker.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jobtracker.jobapplicationtracker.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

//		System.out.println("🔍 JWT Filter Executing...");
//		System.out.println("🔑 Authorization Header: " + request.getHeader("Authorization"));

		if (request.getRequestURI().startsWith("/api/auth/")) {
//			System.out.println("🚫 Skipping authentication for: " + request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		// Extract token
		String token = getTokenFromRequest(request);
		if (token == null) {
//			System.out.println("⚠️ No JWT Token Found!");
			filterChain.doFilter(request, response);
			return;
		}

		// Validate token
		if (jwtUtil.validateToken(token)) {
			String username = jwtUtil.extractUsername(token);
//			System.out.println("✅ Valid Token! Username: " + username);

			// Set authentication
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
					null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} else {
//			System.out.println("❌ Invalid Token!");
		}

		filterChain.doFilter(request, response);
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7); // Remove "Bearer " from the token
		}
		return null;
	}
}
