package com.api.backendCCEP.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(value = "SELECT * FROM products p ORDER BY p.id ASC", nativeQuery = true)
	Page<Product> findAllProductsWithPagination(Pageable pageable);

	// Filtrar el producto por referencia o por nombre
	@Query(value = "SELECT * \r\n"
	        + "FROM products \r\n"
	        + "WHERE CAST(reference AS TEXT) ILIKE CONCAT('%', :value, '%') \r\n"
	        + "   OR name ILIKE CONCAT('%', :value, '%');\r\n", 
	        nativeQuery = true)
	List<Product> searchProductByKeywordOrReference(@Param("value") String value);

	// Filtrar los productos por el proveedor
	@Query(value = "SELECT * FROM products WHERE provider_id = :provider_id", nativeQuery = true)
	List<Product> filterProductsByProviders(@Param("provider_id") long provider_id);
	
	//Encontrar el producto por el nombre exacto
	public Optional<Product> findByName(String name);
	
}
