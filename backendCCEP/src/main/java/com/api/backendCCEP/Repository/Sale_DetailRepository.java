package com.api.backendCCEP.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Sale_Detail;

@Repository
public interface Sale_DetailRepository extends JpaRepository<Sale_Detail, Long> {

	// Listar los detalles por el id de la venta
	@Query(value = "SELECT * FROM sales_details sd where sd.sale_id = :saleId", nativeQuery = true)
	List<Sale_Detail> listSalesDetailsById(@Param("saleId") long saleId);
	
	// Eliminar los detalles basado en el id de la venta
	@Modifying
	@Query(value = "DELETE FROM sales_details WHERE sale_id = :saleId", nativeQuery = true)
	void deleteDetails(@Param("saleId") long saleId);
	
}
