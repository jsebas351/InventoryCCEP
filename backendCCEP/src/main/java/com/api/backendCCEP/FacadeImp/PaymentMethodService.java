package com.api.backendCCEP.FacadeImp;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.IPaymentMethod;
import com.api.backendCCEP.Model.Payment_Method;
import com.api.backendCCEP.Repository.Payment_MethodRepository;

@Service
public class PaymentMethodService implements IPaymentMethod{

	private Payment_MethodRepository methodRepository;
		
	public PaymentMethodService(Payment_MethodRepository methodRepository) {
		this.methodRepository = methodRepository;
	}

	@Override
	@Secured({"ROLE_Administrador", "ROLE_Vendedor"})
	public List<Payment_Method> allPaymentMethods() {
		return methodRepository.findAll();
	}

	@Override
	@Secured({"ROLE_Administrador", "ROLE_Vendedor"})
	public Payment_Method findById(long id) {
		return methodRepository.findById(id).orElse(null);
	}

}
