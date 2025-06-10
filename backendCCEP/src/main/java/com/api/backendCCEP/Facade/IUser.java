package com.api.backendCCEP.Facade;

import com.api.backendCCEP.Model.User;

public interface IUser {

	public User findById(long id);
	public User findByEmail(String email);
	
}
