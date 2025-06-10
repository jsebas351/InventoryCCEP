package com.api.backendCCEP.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api.backendCCEP.Dto.LoginDto;
import com.api.backendCCEP.FacadeImp.UserSecurityService;
import com.api.backendCCEP.Util.ApiResponse;
import com.api.backendCCEP.Util.JwtUtil;

@RestController
@RequestMapping({ "/auth" })
public class AuthenticationController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserSecurityService securityService;

	public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserSecurityService securityService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.securityService = securityService;
	}

	// Inicio de Sesión
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginDto loginDto) {
		ApiResponse<String> response = new ApiResponse<>();

		try {
			UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(loginDto.getEmail(),
					loginDto.getPassword());
			this.authenticationManager.authenticate(login);

			String jwt = jwtUtil.createJwt(loginDto.getEmail());
			
			response.setSuccess(true);
			response.setMessage("Inicio de Sesion Exitoso");
			response.setData(null);
			response.setCode(200);

			return ResponseEntity.ok().header("Authorization", jwt).body(response);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Inicio de Sesion Fallido");
			response.setData(null);
			response.setCode(500);

			return ResponseEntity.internalServerError().body(response);
		}
	}

	//Validacion de token
	@PostMapping("/validate-token")
	public ResponseEntity<ApiResponse<String>> validateToken(@RequestHeader("Authorization") String authHeader) {
	    ApiResponse<String> response = new ApiResponse<>();

	    try {
	        // Validar que el encabezado Authorization esté presente y en formato correcto
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            response.setSuccess(false);
	            response.setMessage("Token no proporcionado o formato incorrecto.");
	            response.setCode(400);
	            return ResponseEntity.badRequest().body(response);
	        }

	        String jwt = authHeader.substring(7); // Remover "Bearer "

	        // Validar el JWT
	        if (!jwtUtil.isValid(jwt)) {
	            response.setSuccess(false);
	            response.setMessage("Sesion Invalida.");
	            response.setCode(401);
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	        }

	        // Obtener el nombre de usuario del JWT
	        String username = jwtUtil.getUsername(jwt);

	        // Validar el usuario
	        if (securityService.isUserValid(username)) {
	            response.setSuccess(true);
	            response.setMessage("Sesion Valida.");
	            response.setCode(200);
	            return ResponseEntity.ok().body(response);
	        } else {
	            response.setSuccess(false);
	            response.setMessage("Usuario inactivo o no válido.");
	            response.setCode(401);
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	        }

	    } catch (UsernameNotFoundException e) {
	        response.setSuccess(false);
	        response.setMessage("Error interno del servidor.");
	        response.setCode(401);
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	    } catch (Exception e) {
	        response.setSuccess(false);
	        response.setMessage("Error interno del servidor.");
	        response.setCode(500);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

}
