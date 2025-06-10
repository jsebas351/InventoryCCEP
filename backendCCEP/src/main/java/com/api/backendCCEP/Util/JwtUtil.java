package com.api.backendCCEP.Util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Component
public class JwtUtil {

	private final Algorithm ALGORITHM;

	public JwtUtil(@Value("${jwt.key.secret}") String secretKey) {
		this.ALGORITHM = Algorithm.HMAC256(secretKey);
	}

	// Método para generar Access Token
	public String createJwt(String username) {
		return JWT.create().withSubject(username).withIssuer("mpccepInventory").withIssuedAt(new Date())
				.withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(3))).sign(ALGORITHM);
	}

	// Validar si el JWT es valido
	public boolean isValid(String jwt) {
		try {
			JWT.require(ALGORITHM).build().verify(jwt);
			return true;
		} catch (JWTVerificationException e) {
			System.err.println("JWT Verification failed: " + e.getMessage());
			return false;
		}
	}

	// Obtener el usuario al que se le asigno el JWT
	public String getUsername(String jwt) {
		return JWT.require(ALGORITHM).build().verify(jwt).getSubject();
	}
	
}
