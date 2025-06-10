package com.api.backendCCEP.Repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long>{

	//listar las ventas con paginacion
    @Query(value = "SELECT * FROM sales s ORDER BY s.id DESC", nativeQuery = true)
    Page<Sale> listSalesWithPagination(Pageable pageable);
    
    // Llamar a la función get_sales_summary
    @Query(value = "SELECT * FROM get_sales_summary(:startDate, :endDate)", nativeQuery = true)
    List<Object[]> getSalesSummary(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
}
