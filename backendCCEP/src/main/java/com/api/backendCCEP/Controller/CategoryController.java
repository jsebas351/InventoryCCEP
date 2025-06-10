package com.api.backendCCEP.Controller;

import java.util.List;
import java.util.Optional;

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
import com.api.backendCCEP.Model.Category;
import com.api.backendCCEP.Model.SubCategory;
import com.api.backendCCEP.Util.ApiResponse;

@RestController
@RequestMapping({ "/admin" })
public class CategoryController {

	// Instancias
	private ICategory icategory;

	public CategoryController(ICategory icategory) {
		this.icategory = icategory;
	}


	// Verificar si ya existe una categoria registrada
	public boolean verifyExistingCategories(Category category) {
		List<Category> existingCategories = icategory.allCategories();
		boolean categoryExists = existingCategories.stream()
				.anyMatch(existingCategory -> existingCategory.getName().equals(category.getName()));
		return categoryExists;
	}

	// Verificar si ya existe una categoria registrada excluyendo la actual
	public boolean verifyExistingCategoriesById(long id, Category category) {
		List<Category> existingCategories = icategory.allCategories();
		boolean categoryExists = existingCategories.stream()
				.anyMatch(existingCategory -> existingCategory.getName().equals(category.getName())
						&& !(existingCategory.getId() == id));
		return categoryExists;
	}

	// Validacion de Campos vacios
	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	// Listar categorias con paginacion
	@GetMapping({ "/categories" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Page<Category>> getCategoriesList(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size) {

		ApiResponse<Page<Category>> response = new ApiResponse<>();

		try {
			Pageable pageable = PageRequest.of(page, size);

			Page<Category> categoriesPage = icategory.categoryList(pageable);

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(categoriesPage);
			response.setCode(200);
			

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta: " + e);
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Listar categorias sin paginacion
	@GetMapping({ "/categoriesnotpaginated" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<List<Category>> getCategoriesListNotPaginated() {

		ApiResponse<List<Category>> response = new ApiResponse<>();

		try {
			List<Category> categories = icategory.allCategories();

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(categories);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Creacion de nuevas Categorias
	@PostMapping({ "/categories/create" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Category> createCategories(@RequestBody Category category) {

		ApiResponse<Category> response = new ApiResponse<>();

		try {
			// Validar campos obligatorios
			if (isNullOrEmpty(category.getName())) {
				response.setSuccess(false);
				response.setMessage("Nombre es un campo obligatorio");
				response.setData(null);
				response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
				// Verficar una Categoria Existente
			} else if (verifyExistingCategories(category)) {
				response.setSuccess(false);
				response.setMessage("Ya existe una categoría con el mismo nombre");
				response.setData(null);
				response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
			} else {
				category.setState("Activo");
				icategory.save(category);

				response.setSuccess(true);
				response.setMessage("Categoría creada exitosamente");
				response.setData(category);
				response.setCode(201); // Código de respuesta 201 para indicar creación exitosa
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al crear la categoría");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Encontrar la categoria por id
	@GetMapping({ "/categories/{id}" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Category> findCategory(@PathVariable Long id) {

		ApiResponse<Category> response = new ApiResponse<>();

		try {
			// Aquí utilizamos el método findById de la interfaz ICategory
			Category category = icategory.findById(id);

			if (category != null) {
				response.setSuccess(true);
				response.setMessage("Categoría encontrada exitosamente");
				response.setData(category);
				response.setCode(200);
			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró la categoría con el ID proporcionado");
				response.setData(null);
				response.setCode(404);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al buscar la categoría");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Actualizar Categorias
	@PutMapping("/categories/update/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Category> updateCategory(@PathVariable long id, @RequestBody Category updatedCategory) {
		ApiResponse<Category> response = new ApiResponse<>();

		try {
			// Verificar si ya existe una categoría con el mismo nombre, excluyendo la
			// categoría actual
			if (verifyExistingCategoriesById(id, updatedCategory)) {
				response.setSuccess(false);
				response.setMessage("Ya existe una categoría con el mismo nombre");
				response.setData(null);
				response.setCode(400);
			} else {
				// Actualizar la categoría solo si existe
				Optional<Category> optionalCategory = Optional.of(icategory.findById(id));

				if (optionalCategory.isPresent()) {
					Category existingCategory = optionalCategory.get();

					// Actualizar los campos de la categoría existente con la información
					// proporcionada
					existingCategory.setName(updatedCategory.getName());
					existingCategory.setState(updatedCategory.getState());

					// Guardar la categoría actualizada
					icategory.save(existingCategory);

					response.setSuccess(true);
					response.setMessage("Categoría actualizada exitosamente");
					response.setData(existingCategory);
					response.setCode(200);
				} else {
					response.setSuccess(false);
					response.setMessage("No se encontró la categoría con el ID proporcionado");
					response.setData(null);
					response.setCode(404);
				}
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al actualizar la categoría");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	@DeleteMapping("/categories/delete/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<String> deleteCategory(@PathVariable Long id) {

		ApiResponse<String> response = new ApiResponse<>();

		try {
			// Verificar si la categoría existe antes de intentar eliminarla
			Category existingCategory = icategory.findById(id);

			if (existingCategory != null) {
				// Verificar si existen subcategorías relacionadas
				List<SubCategory> subCategories = existingCategory.getSubCategories();

				if (subCategories.isEmpty()) {
					icategory.delete(existingCategory);

					response.setSuccess(true);
					response.setMessage("Categoría eliminada exitosamente");
					response.setData("Categoría eliminada");
					response.setCode(200);
				} else {
					response.setSuccess(false);
					response.setMessage("No se puede eliminar una categoría relacionada con una subcategoría");
					response.setData(null);
					response.setCode(400);
				}
			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró la categoría con el ID proporcionado");
				response.setData(null);
				response.setCode(404);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al eliminar la categoría");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

}
