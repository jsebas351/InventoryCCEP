package com.api.backendCCEP.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.backendCCEP.Facade.ICategory;
import com.api.backendCCEP.Facade.ISubCategory;
import com.api.backendCCEP.Model.Category;
import com.api.backendCCEP.Model.Product;
import com.api.backendCCEP.Model.SubCategory;
import com.api.backendCCEP.Util.ApiResponse;

@RestController
@RequestMapping({ "/admin" })
public class SubCategoryController {

	// Instancias
	private ISubCategory iSubCategory;
	private ICategory iCategory;

	public SubCategoryController(ISubCategory iSubCategory, ICategory iCategory) {
		this.iSubCategory = iSubCategory;
		this.iCategory = iCategory;
	}

	// Revisar subcategorias existentes
	public boolean verifyExistingCategories(SubCategory subCategory) {
		List<SubCategory> existingSubCategories = iSubCategory.allSubCategories();
		boolean subCategoryExists = existingSubCategories.stream()
				.anyMatch(existingSubCategory -> existingSubCategory.getName().equals(subCategory.getName()));
		return subCategoryExists;
	}

	// Verificar si existe la subacategoria con el mismo nombre excluyendo esta
	public boolean verifyExistingCategoriesById(long id, SubCategory subCategory) {
		List<SubCategory> existingSubCategories = iSubCategory.allSubCategories();
		boolean categoryExists = existingSubCategories.stream()
				.anyMatch(existingSubCategory -> existingSubCategory.getName().equals(subCategory.getName())
						&& !(existingSubCategory.getId() == id));
		return categoryExists;
	}

	// Validar Campos, nombre y estado
	private boolean isNullOrEmpty(Object[] array) {
		if (array == null) {
			return true; // Si el array es nulo, consideramos que está vacío
		}

		for (Object obj : array) {
			if (obj == null) {
				return true; // Si encontramos un objeto nulo, devolvemos true
			} else if (obj instanceof String && ((String) obj).trim().isEmpty()) {
				return true; // Si encontramos un string vacío, devolvemos true
			}
		}

		return false; // Si ninguno de los objetos en el array es nulo o vacío, devolvemos false
	}

	// Listar SubCategorias con paginacion
	@GetMapping({ "/subcategories" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Page<SubCategory>> getSubCategoriesListPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		ApiResponse<Page<SubCategory>> response = new ApiResponse<>();

		try {
			Pageable pageable = PageRequest.of(page, size);

			Page<SubCategory> subcategories = iSubCategory.listSubCategories(pageable);

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(subcategories);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta " + e);
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Listar SubCategorias
	@GetMapping({ "/subcategoriesnotpaginated" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<List<SubCategory>> getSubCategoriesListNotPaginated() {

		ApiResponse<List<SubCategory>> response = new ApiResponse<>();

		try {
			List<SubCategory> subcategories = iSubCategory.allSubCategories();

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(subcategories);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta " + e);
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Crear nuevas subcategories
	@PostMapping("/subcategories/create")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<SubCategory> createSubCategories(@RequestBody SubCategory subCategory) {

		ApiResponse<SubCategory> response = new ApiResponse<>();

		try {
			// Validar si la categoría asociada existe
			Category existingCategory = iCategory.findById(subCategory.getCategory_id().getId());

			// Array de campos
			Object[] fieldsToValidate = { subCategory.getName(), subCategory.getCategory_id() };

			if (isNullOrEmpty(fieldsToValidate)) {
				response.setSuccess(false);
				response.setMessage("El nombre y la categoria no pueden estar vacíos");
				response.setData(null);
				response.setCode(400);
			} else if (existingCategory == null) {
				response.setSuccess(false);
				response.setMessage("La categoría asociada no existe");
				response.setData(null);
				response.setCode(400);
			} else if (verifyExistingCategories(subCategory)) {
				response.setSuccess(false);
				response.setMessage("Ya existe una SubCategoría con el mismo nombre");
				response.setData(null);
				response.setCode(400);
			} else {
				subCategory.setState("Activo");
				iSubCategory.save(subCategory);

				response.setSuccess(true);
				response.setMessage("SubCategoría creada exitosamente");
				response.setData(subCategory);
				response.setCode(201); // Código de respuesta 201 para indicar creación exitosa
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al crear la SubCategoría " + e.getMessage());
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Actualizar subcategorias existentes
	@PutMapping("/subcategories/update/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<SubCategory> updateSubCategory(@PathVariable Long id,
			@RequestBody SubCategory updatedSubCategory) {
		ApiResponse<SubCategory> response = new ApiResponse<>();

		try {
			// Verificar si la SubCategory existe
			SubCategory existingSubCategory = iSubCategory.findById(id);
			if (existingSubCategory == null) {
				response.setSuccess(false);
				response.setMessage("La SubCategoría no existe");
				response.setData(null);
				response.setCode(404); // Código de respuesta 404 para indicar que no se encontró la SubCategoría
			} else {
				// Validar si la nueva categoría asociada existe
				Category existingCategory = iCategory.findById(updatedSubCategory.getCategory_id().getId());
				if (existingCategory == null) {
					response.setSuccess(false);
					response.setMessage("La nueva categoría asociada no existe");
					response.setData(null);
					response.setCode(400);
				} else if (verifyExistingCategoriesById(id, updatedSubCategory)) {
					response.setSuccess(false);
					response.setMessage("Ya existe otra SubCategoría con el mismo nombre");
					response.setData(null);
					response.setCode(400);
				} else {
					// Actualizar la SubCategory
					existingSubCategory.setName(updatedSubCategory.getName());
					existingSubCategory.setState(updatedSubCategory.getState());
					existingSubCategory.setCategory_id(updatedSubCategory.getCategory_id());

					iSubCategory.save(existingSubCategory);

					response.setSuccess(true);
					response.setMessage("SubCategoría actualizada exitosamente");
					response.setData(existingSubCategory);
					response.setCode(200); // Código de respuesta 200 para indicar éxito en la actualización
				}
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al actualizar la SubCategoría");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Eliminar la categoria encontrada con el id
	@DeleteMapping("/subcategories/delete/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<SubCategory> deleteSubCategory(@PathVariable long id) {

		ApiResponse<SubCategory> response = new ApiResponse<>();

		try {

			SubCategory existingSubCategory = iSubCategory.findById(id);

			if (existingSubCategory != null) {

				// Verificar si existen productos relacionadas
				List<Product> products = existingSubCategory.getProducts();

				if (products.isEmpty()) {
					iSubCategory.delete(existingSubCategory);
					response.setSuccess(true);
					response.setMessage("SubCategoria eliminada existosamente");
					response.setData(existingSubCategory);
					response.setCode(200);
				} else {
					response.setSuccess(false);
					response.setMessage("No se puede eliminar una subcategoria relacionada con uno o mas productos");
					response.setData(null);
					response.setCode(400);
				}
			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró la SuCategoría con el ID proporcionado");
				response.setData(null);
				response.setCode(404);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al eliminar la SubCategoría");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

}
