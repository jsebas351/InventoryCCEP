package com.api.backendCCEP.Facade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.backendCCEP.Model.Purchase;
import com.api.backendCCEP.Model.Purchase_Detail;

public interface IPurchase {

	public Page<Purchase> listPurchases(Pageable pageable);
    public List<Purchase_Detail> listPurchasesDetailsById(long saleId);
    public Purchase findById(long id);
    public void save(Purchase purchase);
    public void saveDetails(Purchase_Detail purchase_Detail);
    public void deletePurchase(Purchase Purchase);
    public void detetePurchaseDetails(long id);
    public void detetePurchasesDetailsUpdate(Purchase_Detail purchase_Detail);
	
}
