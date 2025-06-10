package com.api.backendCCEP.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;

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

import com.api.backendCCEP.Facade.IProduct;
import com.api.backendCCEP.Facade.ISubCategory;
import com.api.backendCCEP.Facade.ISupplier;
import com.api.backendCCEP.Model.Product;
import com.api.backendCCEP.Model.Sale_Detail;
import com.api.backendCCEP.Model.SubCategory;
import com.api.backendCCEP.Model.Supplier;
import com.api.backendCCEP.Util.ApiResponse;

@RestController
@RequestMapping({ "/admin" })
public class ProductController {

	// Instacias
	private IProduct iProduct;
	private ISubCategory iSubCategory;
	private ISupplier iSupplier;

	public ProductController(IProduct iProduct, ISubCategory iSubCategory, ISupplier iSupplier) {
		this.iProduct = iProduct;
		this.iSubCategory = iSubCategory;
		this.iSupplier = iSupplier;
	}

	// Verificar si ya hay un producto registrado con la misma informacion
	public boolean verifyExistingProduct(Object[] array) {
		List<Product> existingProducts = iProduct.allProducts();

		boolean productExists = existingProducts.stream().anyMatch(existingProduct -> {
			for (Object field : array) {

				if (field.equals(existingProduct.getReference()) || field.equals(existingProduct.getName())) {
					return true;
				}

			}
			return false;
		});

		return productExists;
	}

	// Verificar si ya existe un producto registrado excluyendo el actual
	public boolean verifyExistingProductById(long id, Object[] array) {
		List<Product> existingProducts = iProduct.allProducts();

		boolean productExists = existingProducts.stream().anyMatch(existingProduct -> {
			for (Object field : array) {

				if (field.equals(existingProduct.getReference()) || field.equals(existingProduct.getName())) {
					if (existingProduct.getId() != id) {
						return true;
					}
				}
			}
			return false;
		});

		return productExists;
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

	// Listar productos con paginacion
	@GetMapping({ "/products" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Page<Product>> getProductLisPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		ApiResponse<Page<Product>> response = new ApiResponse<>();

		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<Product> products = iProduct.listProduct(pageable);

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(products);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);

		}
		return response;
	}

	// Listar productos sin paginacion
	@GetMapping({ "/productnotpaginated" })
	@PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
	public ApiResponse<List<Product>> getProductListNotPaginated() {

		ApiResponse<List<Product>> response = new ApiResponse<>();

		try {
			List<Product> products = iProduct.allProducts();

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(products);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Creacion de nuevos Productos
	@PostMapping({ "/products/create" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Product> createProducts(@RequestBody Product product) {

		ApiResponse<Product> response = new ApiResponse<>();

		// Validar si la categoría asociada existe
		SubCategory existingSubCategory = iSubCategory.findById(product.getSubcategory_id().getId());
		// Validar si el proveedor asociado existe
		Supplier existingSupplier = iSupplier.findById(product.getProvider_id().getId());

		// Array de campos
		Object[] fieldsToValidate = { product.getReference(), product.getName() };

		try {
			// Validar campos obligatorios
			if (isNullOrEmpty(fieldsToValidate)) {
				response.setSuccess(false);
				response.setMessage("Todos los campos son requeridos");
				response.setData(null);
				response.setCode(400);
			} else if (existingSubCategory == null) {
				response.setSuccess(false);
				response.setMessage("La subcategoría asociada no existe");
				response.setData(null);
				response.setCode(400);
			} else if (existingSupplier == null) {
				response.setSuccess(false);
				response.setMessage("El proveedor asociado no existe");
				response.setData(null);
				response.setCode(400);
			} else if (verifyExistingProduct(fieldsToValidate)) {
				response.setSuccess(false);
				response.setMessage("Ya hay un producto registrado con esa referencia y nombre");
				response.setData(null);
				response.setCode(400);
			} else {

				// Generar referencia automática
				Long salePrice = product.getSale_price();
				String seconds = String.format("%02d", LocalDateTime.now().getSecond());
				String minutes = String.format("%02d", LocalDateTime.now().getMinute());
				String firstTwoDigits = salePrice.toString().substring(0,
						Math.min(2, salePrice.toString().length()));
				String reference = String.format("%02d%s%s%s", product.getSubcategory_id().getId(),
						product.getProvider_id().getId(), firstTwoDigits, seconds + minutes);
				
				product.setReference(Long.parseLong(reference));
				product.setState("Activo");
				iProduct.save(product);

				response.setSuccess(true);
				response.setMessage("Producto creado exitosamente");
				response.setData(product);
				response.setCode(201);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al crear el producto");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Encontrar el producto por id
	@GetMapping({ "/products/{id}" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Product> findProduct(@PathVariable Long id) {

		ApiResponse<Product> response = new ApiResponse<>();

		try {
			// Aquí utilizamos el método findById de la interfaz IProduct
			Product product = iProduct.findById(id);

			if (product != null) {
				response.setSuccess(true);
				response.setMessage("Producto encontrado exitosamente");
				response.setData(product);
				response.setCode(200);
			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró el producto con el ID proporcionado");
				response.setData(null);
				response.setCode(404);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al buscar el producto");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Actualizar Productos
	@PutMapping("/products/update/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Product> updateProduct(@PathVariable long id, @RequestBody Product updatedProduct) {
		ApiResponse<Product> response = new ApiResponse<>();

		// Validar si la categoría asociada existe
		SubCategory existingSubCategory = iSubCategory.findById(updatedProduct.getSubcategory_id().getId());
		// Validar si el proveedor asociado existe
		Supplier existingSupplier = iSupplier.findById(updatedProduct.getProvider_id().getId());
		// Array de campos
		Object[] fieldsToValidate = { updatedProduct.getReference(), updatedProduct.getName() };

		try {
			// Validar campos obligatorios
			if (isNullOrEmpty(fieldsToValidate)) {
				response.setSuccess(false);
				response.setMessage("Todos los campos son requeridos");
				response.setData(null);
				response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
				// Verficar una Categoria Existente
			} else if (existingSubCategory == null) {
				response.setSuccess(false);
				response.setMessage("La subcategoría asociada no existe");
				response.setData(null);
				response.setCode(400);
			} else if (existingSupplier == null) {
				response.setSuccess(false);
				response.setMessage("El proveedor asociado no existe");
				response.setData(null);
				response.setCode(400);
			} else if (verifyExistingProductById(id, fieldsToValidate)) {
				response.setSuccess(false);
				response.setMessage("Ya hay un producto registrado con esa referencia y nombre");
				response.setData(null);
				response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
			} else {
				// Actualizar la producto solo si existe
				Optional<Product> optionalProduct = Optional.of(iProduct.findById(id));

				if (optionalProduct.isPresent()) {
					Product existingProduct = optionalProduct.get();

					// Actualizar los campos del producto existente con la información
					// proporcionada
					existingProduct.setName(updatedProduct.getName());
					existingProduct.setDescription(updatedProduct.getDescription());
					existingProduct.setPurchase_price(updatedProduct.getPurchase_price());
					existingProduct.setSale_price(updatedProduct.getSale_price());
					existingProduct.setSubcategory_id(updatedProduct.getSubcategory_id());
					existingProduct.setProvider_id(updatedProduct.getProvider_id());
					existingProduct.setState(updatedProduct.getState());

					// Guardar el producto actualizado
					iProduct.save(existingProduct);

					response.setSuccess(true);
					response.setMessage("Producto actualizado exitosamente");
					response.setData(existingProduct);
					response.setCode(200);
				} else {
					response.setSuccess(false);
					response.setMessage("No se encontró un producto con el ID proporcionado");
					response.setData(null);
					response.setCode(404);
				}
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al actualizar el producto");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Eliminar al producto encontrado por el id
	@DeleteMapping("/products/delete/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Product> deleteProduct(@PathVariable Long id) {

		ApiResponse<Product> response = new ApiResponse<>();

		// Verificar si existe el producto antes de eliminarlo
		Product existingProduct = iProduct.findById(id);

		try {
			if (existingProduct != null) {

				// Verificar si existe el producto en una venta
				List<Sale_Detail> sale_Details = existingProduct.getSale_Details();

				if (sale_Details.isEmpty()) {
					iProduct.delete(existingProduct);
					response.setSuccess(true);
					response.setMessage("Producto eliminado exitosamente");
					response.setData(null);
					response.setCode(200);
				} else {
					response.setSuccess(false);
					response.setMessage("No se puede eliminar un producto vendido");
					response.setData(null);
					response.setCode(400);
				}

			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró el producto con el ID proporcionado");
				response.setData(null);
				response.setCode(404);
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al eliminar el producto");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Filtrar producto por Referencia o nombre(Ventas)
	@PostMapping("/products/search")
	@PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
	public ApiResponse<List<Product>> searchProductByReferenceOrName(@RequestBody Map<String, Object> request) {

		ApiResponse<List<Product>> response = new ApiResponse<>();

		try {

			String reference = (String) request.get("reference");
			String name = (String) request.get("name");

			if (reference == null && name == null) {
				response.setSuccess(false);
				response.setMessage("Debe proporcionar al menos un campo para realizar la búsqueda");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			String value = reference != null ? reference : name;

			// Realizar la consulta basada en el campo proporcionado
			List<Product> products = iProduct.searchProductByReferenceOrName(value);

			if (products.isEmpty()) {
				response.setSuccess(false);
				response.setMessage("No se encontró el producto");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(products);
			response.setCode(200);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error en la consulta: " + e.getMessage());
			response.setData(null);
			response.setCode(500);
		}
		return response;
	}

	// Filtrar los productos por el proveedor(Compras)
	@GetMapping("/products/filter-product-provider/{provider_id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<List<Product>> searchProductByReferenceOrName(@PathVariable Long provider_id) {

		ApiResponse<List<Product>> response = new ApiResponse<>();

		try {

			List<Product> filterProducts = iProduct.filterProductsByProviders(provider_id);

			if (filterProducts.isEmpty()) {
				response.setSuccess(false);
				response.setMessage("No se encontró productos asociados a ese proveedor");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(filterProducts);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta: " + e.getMessage());
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

}
