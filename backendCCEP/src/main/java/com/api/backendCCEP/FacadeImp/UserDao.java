package com.api.backendCCEP.FacadeImp;

import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.IUser;
import com.api.backendCCEP.Model.User;
import com.api.backendCCEP.Repository.UserRepository;

@Service
public class UserDao implements IUser{

	public UserRepository userRepository;
	
	public UserDao(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User findById(long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
