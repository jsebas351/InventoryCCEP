package com.api.backendCCEP.FacadeImp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.ISubCategory;
import com.api.backendCCEP.Model.SubCategory;
import com.api.backendCCEP.Repository.SubCategoryRepository;

@Service
public class SubCategoryDao implements ISubCategory {

	// Instancias
	private SubCategoryRepository subCategoryRepository;

	public SubCategoryDao(SubCategoryRepository subCategoryRepository) {
		this.subCategoryRepository = subCategoryRepository;
	}

	// Usar la instancia del repositorio y obtener el metodo subCategories
	@Override
	@Secured("ROLE_Administrador")
	public Page<SubCategory> listSubCategories(Pageable pageable) {
		return subCategoryRepository.findAllSubCategoriesWithPagination(pageable);
	}
	
	@Override
	@Secured("ROLE_Administrador")
	public List<SubCategory> allSubCategories() {
		return subCategoryRepository.findAll();
	}

	// Llamar el metodo findById del repository
	@Override
	@Secured("ROLE_Administrador")
	public SubCategory findById(long id) {
		return subCategoryRepository.findById(id).orElse(null);
	}

	//Obtener el metodo save para guardar las subcategorias
	@Override
	@Secured("ROLE_Administrador")
	public void save(SubCategory subCategory) {
		subCategoryRepository.save(subCategory);
	}

	//Obtener la subcategoria por el id y eliminar la categoria encontrada
	@Override
	@Secured("ROLE_Administrador")
	public void delete(SubCategory subCategory) {
		SubCategory subcat = subCategoryRepository.findById(subCategory.getId()).orElse(null);
		subCategoryRepository.delete(subcat);
	}

}
