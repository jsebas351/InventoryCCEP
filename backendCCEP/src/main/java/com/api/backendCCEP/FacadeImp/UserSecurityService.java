package com.api.backendCCEP.FacadeImp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Model.Rol;
import com.api.backendCCEP.Model.User;
import com.api.backendCCEP.Repository.UserRepository;

@Service
public class UserSecurityService implements UserDetailsService {

	private final UserRepository userRepository;

	public UserSecurityService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);

		if (user == null) {
			throw new UsernameNotFoundException("Usuario o Contraseña Invalidos");
		} else if ("Inactivo".equals(user.getState())) {
			throw new UsernameNotFoundException("El usuario no tiene permisos");
		}

		String[] roles = user.getRoles().stream().map(Rol::getName_role).toArray(String[]::new);

		return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
				.password(user.getPassword_encrypted()).authorities(this.grantedAuthorities(roles)).build();
	}

	// Metodo para asignar permisos especificos
	private String[] getAuthorities(String rol) {
		if ("Administrador".equals(rol) || "Vendedor".equals(rol)) {
			return new String[] { "random_authorities" };
		}

		return new String[] {};
	}

	private List<GrantedAuthority> grantedAuthorities(String[] roles) {
		List<GrantedAuthority> authorities = new ArrayList<>(roles.length);

		for (String rol : roles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));

			for (String authority : this.getAuthorities(rol)) {
				authorities.add(new SimpleGrantedAuthority(authority));
			}
		}

		return authorities;
	}

	public boolean isUserValid(String username) {
		User user = userRepository.findByEmail(username);

		if (user == null) {
			throw new UsernameNotFoundException("Error");
		}

		if ("Inactivo".equals(user.getState())) {
			throw new UsernameNotFoundException("Error");
		}

		return true;
	}

}
