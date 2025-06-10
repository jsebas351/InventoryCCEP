package com.api.backendCCEP.Facade;

import java.util.List;

import com.api.backendCCEP.Model.Payment_Method;

public interface IPaymentMethod {

	public List<Payment_Method> allPaymentMethods();
	public Payment_Method findById(long id);
	
}
