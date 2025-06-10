package com.api.backendCCEP.FacadeImp;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api.backendCCEP.Util.JwtUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsService detailsService;

	public JWTFilter(JwtUtil jwtUtil, UserDetailsService detailsService) {
		this.jwtUtil = jwtUtil;
		this.detailsService = detailsService;
	}

	// Creacion de filtros propios
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// Validar que el header "Authorization" sea valido
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if(authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		//Validar que el jwt sea valido
		String jwt = authHeader.replace("Bearer ", "");
		
		if(!this.jwtUtil.isValid(jwt)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		//Decodificar el token recibido
		DecodedJWT decodedJWT = JWT.decode(jwt);
		Date issuedAt = decodedJWT.getIssuedAt();
        Date now = new Date();
        
        // Verificar si el token fue emitido en las últimas 4 horas
        long diffInMillis = now.getTime() - issuedAt.getTime();
        long sixHoursInMillis = TimeUnit.HOURS.toMillis(4);
        
        if (diffInMillis > sixHoursInMillis) {
            filterChain.doFilter(request, response);
            return;
        }
		
		//Cargar el usuario del UserDetailsService
		String email = this.jwtUtil.getUsername(jwt);
		
		User user = (User) this.detailsService.loadUserByUsername(email);
		
		//Cargar el usuario en el contexto de seguridad
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),
				user.getPassword(), user.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		filterChain.doFilter(request, response);
		
	}

}
