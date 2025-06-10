package com.api.backendCCEP.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Purchase_Detail;

@Repository
public interface Purchase_DetailRepository extends JpaRepository<Purchase_Detail, Long> {

	// Listar los detalles por el id de la compra
	@Query(value = "SELECT * FROM purchases_details pd where pd.purchase_id = :purchaseId", nativeQuery = true)
	List<Purchase_Detail> listPurchasesDetailsById(@Param("purchaseId") long purchaseId);

	// Eliminar los detalles basado en el id de la compra
	@Modifying
	@Query(value = "DELETE FROM purchases_details WHERE purchase_id = :purchaseId", nativeQuery = true)
	void deleteDetails(@Param("purchaseId") long purchaseId);

}
