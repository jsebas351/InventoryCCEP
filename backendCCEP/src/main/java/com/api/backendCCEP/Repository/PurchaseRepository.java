package com.api.backendCCEP.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

	//Listar las compras con paginacion
	@Query(value = "SELECT * FROM purchases ORDER BY id DESC", nativeQuery = true)
    Page<Purchase> listPurchasesWithPagination(Pageable pageable);
	
}
