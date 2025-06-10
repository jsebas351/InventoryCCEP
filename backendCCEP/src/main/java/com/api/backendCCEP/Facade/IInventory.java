package com.api.backendCCEP.Facade;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.backendCCEP.Model.Inventory;

public interface IInventory {

	public void save(Inventory inventory);
	public Page<Inventory> stock(Pageable pageable);
	public Optional<Inventory> findInvetoryBySale(long id);
	public Optional<Inventory> findInvetoryByPurchase(long id);
	public Optional<Inventory> findInvetoryByEntry(long id);
	public void deleteInventoryBySale(long id);
	public void deleteInventoryByPurchase(long id);
	public void deleteInventoryByEntry(long id);
}
