package com.api.backendCCEP.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

	// Consulta personalizada para obtener SubCategories con el nombre de la
	// categoría
	@Query(value = "SELECT sc.id, sc.name, c.name as categoryName, sc.category_id, sc.state FROM subcategories"
			+ " sc JOIN categories c on sc.category_id = c.id ORDER BY sc.id ASC", nativeQuery = true)
	Page<SubCategory> findAllSubCategoriesWithPagination(Pageable pageable);

	//Encontrar la subcategoria por el nombre exacto
	public Optional<SubCategory> findByName(String name);
}
