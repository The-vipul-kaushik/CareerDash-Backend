//package com.jobtracker.jobapplicationtracker.utils;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.function.Function;
//
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//
//@Component
//public class JwtUtil {
//
//	private final String SECRET_KEY = "sG7dS+fJk8klwR9nYl6rJ9Q+5I4YP0fs9yTpG1gtB6g=";
//
//	private Key getSigningKey() {
//		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//		return Keys.hmacShaKeyFor(keyBytes);
//	}
//
//	// Generate a token for a given username
//	public String generateToken(String username) {
//		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
//				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
//				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
//	}
//
//	// Validate a token
//	public boolean validateToken(String token) {
//		try {
//			extractAllClaims(token);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	// Extract username from the token
//	public String extractUsername(String token) {
//		return extractClaim(token, Claims::getSubject);
//	}
//
//	// Extract a specific claim using a resolver function
//	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//		final Claims claims = extractAllClaims(token);
//		return claimsResolver.apply(claims);
//	}
//
//	// Extract all claims from the token
//	private Claims extractAllClaims(String token) {
//		return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
//	}
//}
