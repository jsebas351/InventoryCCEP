package com.api.backendCCEP.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.backendCCEP.Facade.IUser;
import com.api.backendCCEP.Model.User;
import com.api.backendCCEP.Util.ApiResponse;

@RestController
@RequestMapping("/user")
public class UserController {

	private IUser iUser;
	
	public UserController(IUser iUser) {
		this.iUser = iUser;
	}

	// Obtener la información del usuario por su ID, solo si el ID coincide con el del usuario autenticado
    @GetMapping("/findByEmail/{email}")
    @PreAuthorize("#email == authentication.name")
    public ApiResponse<User> getUserByEmail(@PathVariable String email, Authentication authentication) {
        ApiResponse<User> response = new ApiResponse<>();
        
        try {
            // Obtener el usuario autenticado a partir del nombre de usuario (email)
            String authenticatedUsername = (String) authentication.getPrincipal();
            User authenticatedUser = iUser.findByEmail(authenticatedUsername);

            // Verificar si el ID solicitado coincide con el del usuario autenticado
            if (authenticatedUser == null || !authenticatedUser.getEmail().equals(email)) {
                response.setSuccess(false);
                response.setMessage("Acceso denegado.");
                response.setCode(403);
                return response;
            }

            // Retornar la información del usuario autenticado
            response.setSuccess(true);
            response.setMessage("Consulta exitosa");
            response.setData(authenticatedUser);
            response.setCode(200);
            return response;

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error en la consulta: " + e.getMessage());
            response.setData(null);
            response.setCode(500);
            return response;
        }
    }
	
}
