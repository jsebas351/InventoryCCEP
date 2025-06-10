package com.api.backendCCEP.FacadeImp;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.IInventory;
import com.api.backendCCEP.Model.Inventory;
import com.api.backendCCEP.Repository.InventoryRepository;

@Service
public class InventoryDao implements IInventory{

	private InventoryRepository inventoryRepository;
	
	public InventoryDao(InventoryRepository inventoryRepository) {
		this.inventoryRepository = inventoryRepository;
	}

	@Override
	@Secured({"ROLE_Administrador", "ROLE_Vendedor"})
	public void save(Inventory inventory) {
		inventoryRepository.save(inventory);
	}

	@Override
	@Secured({"ROLE_Administrador"})
	public Page<Inventory> stock(Pageable pageable) {
		return this.inventoryRepository.stock(pageable);
	}

	@Override
	public Optional<Inventory> findInvetoryBySale(long id) {
		return inventoryRepository.findBySale(id);
	}
	
	@Override
	public Optional<Inventory> findInvetoryByPurchase(long id) {
		return inventoryRepository.findByPurchase(id);
	}
	
	@Override
	public Optional<Inventory> findInvetoryByEntry(long id) {
		return inventoryRepository.findByEntry(id);
	}

	@Override
	@Secured({"ROLE_Administrador", "ROLE_Vendedor"})
	public void deleteInventoryBySale(long id) {
		Inventory inventory = inventoryRepository.findBySale(id).orElse(null);
		inventoryRepository.delete(inventory);
	}

	@Override
	@Secured({"ROLE_Administrador"})
	public void deleteInventoryByPurchase(long id) {
		Inventory inventory = inventoryRepository.findByPurchase(id).orElse(null);
		inventoryRepository.delete(inventory);
	}
	
	@Override
	@Secured({"ROLE_Administrador"})
	public void deleteInventoryByEntry(long id) {
		Inventory inventory = inventoryRepository.findByEntry(id).orElse(null);
		inventoryRepository.delete(inventory);
	}
	
}
