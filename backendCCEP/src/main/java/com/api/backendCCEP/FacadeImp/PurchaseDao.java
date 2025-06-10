package com.api.backendCCEP.FacadeImp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.IPurchase;
import com.api.backendCCEP.Model.Purchase;
import com.api.backendCCEP.Model.Purchase_Detail;
import com.api.backendCCEP.Repository.PurchaseRepository;
import com.api.backendCCEP.Repository.Purchase_DetailRepository;

import jakarta.transaction.Transactional;

@Service
public class PurchaseDao implements IPurchase {

	// Instancias
	private PurchaseRepository purchaseRepository;
	private Purchase_DetailRepository detailRepository;

	public PurchaseDao(PurchaseRepository purchaseRepository, Purchase_DetailRepository detailRepository) {
		this.purchaseRepository = purchaseRepository;
		this.detailRepository = detailRepository;
	}

	@Override
	@Secured("ROLE_Administrador")
	public Page<Purchase> listPurchases(Pageable pageable) {
		return purchaseRepository.listPurchasesWithPagination(pageable);
	}

	@Override
	@Secured("ROLE_Administrador")
	public List<Purchase_Detail> listPurchasesDetailsById(long saleId) {
		return detailRepository.listPurchasesDetailsById(saleId);
	}

	@Override
	@Secured("ROLE_Administrador")
	public Purchase findById(long id) {
		return purchaseRepository.findById(id).orElse(null);
	}

	@Override
	@Secured("ROLE_Administrador")
	public void save(Purchase purchase) {
		purchaseRepository.save(purchase);
	}

	@Override
	@Secured("ROLE_Administrador")
	public void saveDetails(Purchase_Detail purchase_Detail) {
		detailRepository.save(purchase_Detail);
	}

	@Override
	@Secured("ROLE_Administrador")
	public void deletePurchase(Purchase purchase) {
		purchaseRepository.delete(purchase);
	}

	@Override
	@Transactional
	@Secured("ROLE_Administrador")
	public void detetePurchaseDetails(long id) {
		detailRepository.deleteDetails(id);
	}

	// Se crea un nuevo metodo para borrar un solo detalle de la compra
	// el anterior metodo borra todos los detalles asociados a la compra
	@Override
	@Secured("ROLE_Administrador")
	public void detetePurchasesDetailsUpdate(Purchase_Detail purchase_Detail) {
		Purchase_Detail detail = detailRepository.findById(purchase_Detail.getId()).orElse(null);
		detailRepository.delete(detail);
	}

}
