package com.api.backendCCEP.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.api.backendCCEP.FacadeImp.JWTFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig {
	
	private final JWTFilter filter;
	
	public SecurityConfig(JWTFilter filter) {
		this.filter = filter;
	}

	//Configuracion de Spring Security
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		http
		.csrf(csrf -> csrf.disable())
		.cors(Customizer.withDefaults())
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	    .authorizeHttpRequests((authorize) -> authorize
	    	.requestMatchers("/admin/productnotpaginated").hasAnyRole("Administrador", "Vendedor")
	    	.requestMatchers("/admin/products/search").hasAnyRole("Administrador", "Vendedor")
	    	.requestMatchers("/vendor/**").hasAnyRole("Administrador", "Vendedor")
	    	.requestMatchers("/admin/**").hasRole("Administrador")
	    	.requestMatchers("/user/**").authenticated()
	    	.requestMatchers("/auth/**").permitAll()
	        .anyRequest()
	        .authenticated()
	    )
        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManagera(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
