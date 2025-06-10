package com.api.backendCCEP.FacadeImp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.ICategory;
import com.api.backendCCEP.Model.Category;
import com.api.backendCCEP.Repository.CategoryRepository;

@Service
public class CategoryDao implements ICategory {
	
	// Instancias
	private CategoryRepository categoryRepository;

	public CategoryDao(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	// Instanciar la consulta personalizada del repository
	@Override
	@Secured("ROLE_Administrador")
	public Page<Category> categoryList(Pageable pageable) {
		return categoryRepository.findAllCategoriesWithPagination(pageable);
	}

	@Override
	@Secured("ROLE_Administrador")
	public List<Category> allCategories() {
		return categoryRepository.findAll();
	}
	
	// Llamar el metodo findById del repository
	@Override
	@Secured("ROLE_Administrador")
	public Category findById(Long id) {
		return categoryRepository.findById(id).orElse(null);
	}

	// Obtener el metodo save para guardar las categorias
	@Override
	@Secured("ROLE_Administrador")
	public void save(Category category) {
		categoryRepository.save(category);
	}

	// Obtener la categoria por el id y eliminar la categoria encontrada
	@Override
	@Secured("ROLE_Administrador")
	public void delete(Category category) {
		Category cat = categoryRepository.findById(category.getId()).orElse(null);
		categoryRepository.delete(cat);
	}

}
