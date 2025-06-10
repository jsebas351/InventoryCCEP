package com.api.backendCCEP.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>{

	@Query(value = "SELECT MIN(inven.id) AS id, MIN(inven.entry_id) AS entry_id, MIN(inven.loss_id) AS loss_id, MIN(inven.purchasedetail_id) AS purchasedetail_id, "
			+ "MIN(inven.saledetail_id) AS saledetail_id, SUM(inven.stock) AS stock, inven.product_id \r\n"
	        + "FROM inventories inven \r\n"
	        + "GROUP BY inven.product_id \r\n"
	        + "ORDER BY stock DESC;", nativeQuery = true)
	Page<Inventory> stock(Pageable pageable);

	@Query(value = "SELECT i.id, i.product_id, i.saledetail_id, i.stock, i.purchasedetail_id"
			+ " FROM inventories i WHERE i.saledetail_id = :saleId", nativeQuery = true)
	public Optional<Inventory> findBySale(@Param("saleId") long saleId);
	
	@Query(value = "SELECT i.id, i.product_id, i.saledetail_id, i.stock, i.purchasedetail_id"
			+ " FROM inventories i WHERE i.purchasedetail_id = :purchaseId", nativeQuery = true)
	public Optional<Inventory> findByPurchase(@Param("purchaseId") long purchaseId);
	
	@Query(value = "SELECT i.id, i.product_id, i.saledetail_id, i.stock, i.purchasedetail_id,"
			+ " i.entry_id, i.loss_id"
			+ " FROM inventories i WHERE i.entry_id = :entryId", nativeQuery = true)
	public Optional<Inventory> findByEntry(@Param("entryId") long entryId);
}
