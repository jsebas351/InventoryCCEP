package com.api.backendCCEP.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

import com.api.backendCCEP.Facade.IInventory;
import com.api.backendCCEP.Facade.IProduct;
import com.api.backendCCEP.Facade.IPurchase;
import com.api.backendCCEP.Facade.ISupplier;
import com.api.backendCCEP.Model.Inventory;
import com.api.backendCCEP.Model.Product;
import com.api.backendCCEP.Model.Purchase;
import com.api.backendCCEP.Model.Purchase_Detail;
import com.api.backendCCEP.Model.Supplier;
import com.api.backendCCEP.Util.ApiResponse;

@RestController
@RequestMapping({ "/admin" })
public class PurchaseController {

	private IPurchase iPurchase;
	private IProduct iProduct;
	private ISupplier iSupplier;
	private IInventory iInventory;

	public PurchaseController(IPurchase iPurchase, IProduct iProduct, ISupplier iSupplier, IInventory iInventory) {
		this.iPurchase = iPurchase;
		this.iProduct = iProduct;
		this.iSupplier = iSupplier;
		this.iInventory = iInventory;
	}

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

	// Listar Compras
	@GetMapping({ "/purchases" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Page<Purchase>> getPurchaseListPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		ApiResponse<Page<Purchase>> response = new ApiResponse<>();

		try {

			Pageable pageable = PageRequest.of(page, size);

			Page<Purchase> purchases = iPurchase.listPurchases(pageable);

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(purchases);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Obtener la compra por el id
	@GetMapping({ "/purchases/{id}" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Purchase> getPurchaseById(@PathVariable int id) {

		ApiResponse<Purchase> response = new ApiResponse<>();

		try {

			Purchase purchase = iPurchase.findById(id);

			if (purchase == null) {

				response.setSuccess(false);
				response.setMessage("No se encontro la compra");
				response.setData(null);
				response.setCode(404);

			}

			response.setSuccess(true);
			response.setMessage("Consulta Exitosa");
			response.setData(purchase);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Filtrar los detalles basado en el id de la compra
	@GetMapping("/purchase/detailsbyid/{purchaseId}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<List<Purchase_Detail>> getPurchaseDetailsById(@PathVariable int purchaseId) {

		ApiResponse<List<Purchase_Detail>> response = new ApiResponse<>();

		try {

			List<Purchase_Detail> purchaseDetails = iPurchase.listPurchasesDetailsById(purchaseId);

			// Encontrar la venta por el id
			Purchase purchase = iPurchase.findById(purchaseId);

			// Si no se encuentra la venta mostrar la validacion
			if (purchase == null) {
				response.setSuccess(false);
				response.setMessage("No se encontro la compra");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			response.setSuccess(true);
			response.setMessage("Consulta Exitosa");
			response.setData(purchaseDetails);
			response.setCode(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Registrar Compras
	@PostMapping({ "/purchase/create" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Purchase> savePurchaseWithDetails(@RequestBody Map<String, Object> request) {

		ApiResponse<Purchase> response = new ApiResponse<>();

		try {

			Object[] fieldsToValidatePurchase = { request.get("provider_id"), request.get("billNumber") };

			// Validar que el campo proveedor y numero de factura no esten vacios
			if (isNullOrEmpty(fieldsToValidatePurchase)) {
				response.setSuccess(false);
				response.setMessage(
						"Los campos 'proveedor' y 'Numero de factura' son obligatorios y no puede estar vacío.");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			// Cargar completamente el proveedor antes de asignarlo a la compra
			Supplier supplier = iSupplier.findById((int) request.get("provider_id"));
			if (supplier == null) {
				response.setSuccess(false);
				response.setMessage("El proveedor seleccionado no existe");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			// Validar que el campo details no esté vacío y sea una lista
			if (!request.containsKey("details") || !(request.get("details") instanceof List)) {
				response.setSuccess(false);
				response.setMessage("El objeto 'details' es obligatorio y debe ser una lista de detalles.");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			// Convierte el objeto de detalles del request a un array
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> details = (List<Map<String, Object>>) request.get("details");

			// Validar que la lista de detalles no esté vacía
			if (details.isEmpty()) {
				response.setSuccess(false);
				response.setMessage("La lista de detalles no puede estar vacía.");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			// Bloque de codigo de validacion de los detalles
			for (Map<String, Object> detailMap : details) {

				Object[] fieldsToValidateDetail = { detailMap.get("quantity"), detailMap.get("product_id") };

				// Validar que los campos de cada detalle no estén vacíos
				if (isNullOrEmpty(fieldsToValidateDetail)) {
					response.setSuccess(false);
					response.setMessage("Los campos 'Cantidad' y 'Producto' son obligatorios y no pueden estar vacíos");
					response.setData(null);
					response.setCode(400);
					return response;
				}

				// Cargar completamente el producto para validar si existe
				Product product = iProduct.findById((Integer) detailMap.get("product_id"));
				if (product == null) {
					response.setSuccess(false);
					response.setMessage("El producto seleccionado no existe.");
					response.setData(null);
					response.setCode(404);
					return response;
				}

				// Validar que la cantidad no sea cero
				Integer quantity = (Integer) detailMap.get("quantity");
				if (quantity == null || quantity <= 0) {
					response.setSuccess(false);
					response.setMessage("La cantidad debe ser mayor que cero.");
					response.setData(null);
					response.setCode(400);
					return response;
				}

			}

			// Inicializar la variable total y discount
			long total = 0;

			// Instanciar el objeto Purchase
			Purchase purchase = new Purchase();

			// Llenar los campos de la compra
			purchase.setPurchase_date(new Date());
			purchase.setTotal_purchase(total);
			purchase.setBill_number((Integer) request.get("billNumber"));
			purchase.setProvider_id(supplier);
			purchase.setState("Activo");
			iPurchase.save(purchase);

			for (Map<String, Object> map : details) {

				// Inicializar la variable del subtotal
				long subtotal = 0;

				// Instanciar el objeto purchase detail
				Purchase_Detail purchase_Detail = new Purchase_Detail();

				// Cargar completamente el producto antes de asignarlo al detalle
				Product product = iProduct.findById((Integer) map.get("product_id"));

				// Definir las variables para calcular el subtotal
				long purchasePrice = product.getPurchase_price();

				// Definir el campo cantidad
				Integer quantity = (Integer) map.get("quantity");

				// Calcular el subtotal de la compra
				subtotal = ((Integer) map.get("quantity")) * purchasePrice;

				// Si el subtotal es negativo establecerlo como cero
				subtotal = Math.max(subtotal, 0);

				// Agregar el subtotal al total de la compra
				total += subtotal;

				// Llenar los campos de los detalles
				purchase_Detail.setPurchase_id(purchase);
				purchase_Detail.setQuantity(quantity);
				purchase_Detail.setProduct_id(product);
				purchase_Detail.setSubtotal(subtotal);
				iPurchase.saveDetails(purchase_Detail);

				Inventory inventory = new Inventory();
				inventory.setProduct_id(product);
				inventory.setPurchasedetail_id(purchase_Detail);
				inventory.setStock(quantity);
				iInventory.save(inventory);

			}

			// Si el total es negativo después de aplicar el descuento total, establecerlo
			// como cero
			total = Math.max(total, 0);

			purchase.setTotal_purchase(total);
			iPurchase.save(purchase);

			response.setSuccess(true);
			response.setMessage("Compra Guardada exitosamente");
			response.setData(purchase);
			response.setCode(201);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al guardar la compra: " + e);
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Actualizar la compra con los detalles
	@PutMapping("/purchase/update/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Purchase> updatePurchasesWithDetails(@PathVariable int id,
			@RequestBody Map<String, Object> request) {

		ApiResponse<Purchase> response = new ApiResponse<>();

		try {

			// Buscar la compra existente
			Optional<Purchase> existingPurchase = Optional.of(iPurchase.findById(id));
			if (!existingPurchase.isPresent()) {
				response.setSuccess(false);
				response.setMessage("La compra con el ID proporcionado no se encuentra.");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			Object[] fieldsToValidatePurchase = { request.get("provider_id"), request.get("billNumber") };

			// Validar que el campo proveedor y numero de factura no esten vacios
			if (isNullOrEmpty(fieldsToValidatePurchase)) {
				response.setSuccess(false);
				response.setMessage(
						"Los campos 'proveedor' y 'Numero de factura' son obligatorios y no puede estar vacío.");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			// Cargar completamente el proveedor antes de asignarlo a la compra
			Supplier supplier = iSupplier.findById((int) request.get("provider_id"));
			if (supplier == null) {
				response.setSuccess(false);
				response.setMessage("El proveedor seleccionado no existe");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			// Validar que el campo details no esté vacío y sea una lista
			if (!request.containsKey("details") || !(request.get("details") instanceof List)) {
				response.setSuccess(false);
				response.setMessage("El objeto 'details' es obligatorio y debe ser una lista de detalles.");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			// Convierte el objeto de detalles del request a un array
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> details = (List<Map<String, Object>>) request.get("details");

			// Validar que la lista de detalles no esté vacía
			if (details.isEmpty()) {
				response.setSuccess(false);
				response.setMessage("La lista de detalles no puede estar vacía.");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			// Bloque de codigo de validacion de los detalles
			for (Map<String, Object> detailMap : details) {

				Object[] fieldsToValidateDetail = { detailMap.get("quantity"), detailMap.get("product_id") };

				// Validar que los campos de cada detalle no estén vacíos
				if (isNullOrEmpty(fieldsToValidateDetail)) {
					response.setSuccess(false);
					response.setMessage("Los campos 'Cantidad' y 'Producto' son obligatorios y no pueden estar vacíos");
					response.setData(null);
					response.setCode(400);
					return response;
				}

				// Cargar completamente el producto para validar si existe
				Product product = iProduct.findById((Integer) detailMap.get("product_id"));
				if (product == null) {
					response.setSuccess(false);
					response.setMessage("El producto seleccionado no existe.");
					response.setData(null);
					response.setCode(404);
					return response;
				}

				// Validar que la cantidad no sea cero
				Integer quantity = (Integer) detailMap.get("quantity");
				if (quantity == null || quantity <= 0) {
					response.setSuccess(false);
					response.setMessage("La cantidad debe ser mayor que cero.");
					response.setData(null);
					response.setCode(400);
					return response;
				}

			}

			// Inicializar la variable total
			long total = 0;

			// Instaciar el existingPurchase
			Purchase updatePurchase = existingPurchase.get();

			// Llenar los campos de la compra
			updatePurchase.setEdit_date(new Date());
			updatePurchase.setTotal_purchase(total);
			updatePurchase.setBill_number((Integer) request.get("billNumber"));
			updatePurchase.setProvider_id(supplier);
			updatePurchase.setState((String) request.get("status"));
			iPurchase.save(updatePurchase);

			// Obtener los detalles existente para editarlos
			List<Purchase_Detail> existingDetails = iPurchase.listPurchasesDetailsById(id);

			// Lista para almacenar los nuevos detalles
			List<Purchase_Detail> newDetails = new ArrayList<>();

			// Recorrer el objeto detalles
			for (Map<String, Object> detailMap : details) {

				// Obtener el ID del detalle de la solicitud
				Integer detailId = (Integer) detailMap.get("id");

				// Buscar si el detalle existe en los detalles existentes
				Purchase_Detail existingDetail = null;
				for (Purchase_Detail detail : existingDetails) {
					if (detail.getId() == detailId) {
						existingDetail = detail;
						break;
					}
				}

				// Inicializar la variable del subtotal
				long subtotal = 0;

				// Cargar completamente el producto antes de asignarlo al detalle
				Product product = iProduct.findById((Integer) detailMap.get("product_id"));

				// Definir las variables para calcular el subtotal
				long purchasePrices = product.getPurchase_price();

				// Definir el campo cantidad
				Integer quantity = (Integer) detailMap.get("quantity");

				// Calcular el subtotal de la compra
				subtotal = ((Integer) detailMap.get("quantity")) * purchasePrices;

				// Si el subtotal es negativo establecerlo como cero
				subtotal = Math.max(subtotal, 0);

				// Agregar el subtotal al total de la compra
				total += subtotal;

				// Si el detalle existe, actualizarlo
				if (existingDetail != null) {

					// Llenar los campos
					existingDetail.setPurchase_id(updatePurchase);
					existingDetail.setProduct_id(product);
					existingDetail.setQuantity(quantity);
					existingDetail.setSubtotal(subtotal);
					iPurchase.saveDetails(existingDetail);

					Inventory existingInventory = iInventory.findInvetoryByPurchase(existingDetail.getId()).orElse(null);

					existingInventory.setProduct_id(product);
					existingInventory.setStock(quantity);
					iInventory.save(existingInventory);

				} else {

					// Si el detalle no existe, crear uno nuevo y agregarlo a la lista de nuevos
					// detalles
					Purchase_Detail newDetail = new Purchase_Detail();

					newDetail.setQuantity(quantity);
					newDetail.setProduct_id(product);
					newDetail.setSubtotal(subtotal);

					// Agregar el nuevo detalle a la lista de nuevos detalles
					newDetails.add(newDetail);
				}

			}

			// Eliminar los detalles existentes que no están presentes en los nuevos
			// detalles
			for (Purchase_Detail detail : existingDetails) {
				boolean existsInNewDetails = false;
				for (Map<String, Object> detailMap : details) {
					Integer detailId = (Integer) detailMap.get("id");
					if (detail.getId() == detailId) {
						existsInNewDetails = true;
						break;
					}
				}
				// Si el detalle no existe en los nuevos detalles, eliminarlo
				if (!existsInNewDetails) {
					// Eliminar los detalles del inventario
					iInventory.deleteInventoryByPurchase(detail.getId());
					iPurchase.detetePurchasesDetailsUpdate(detail);
				}
			}

			// Agregar los nuevos detalles que no existen en la venta
			for (Purchase_Detail newDetail : newDetails) {
				// Asignar la venta a la que pertenecen los nuevos detalles
				newDetail.setPurchase_id(updatePurchase);
				// Guardar los nuevos detalles
				iPurchase.saveDetails(newDetail);
				// Instacia del inventario
				Inventory newInventory = new Inventory();

				newInventory.setProduct_id(newDetail.getProduct_id());
				newInventory.setPurchasedetail_id(newDetail);
				newInventory.setStock(newDetail.getQuantity());
				iInventory.save(newInventory);
			}

			// Si el total es negativo después de aplicar el descuento total, establecerlo
			// como cero
			total = Math.max(total, 0);

			updatePurchase.setTotal_purchase(total);
			iPurchase.save(updatePurchase);

			response.setSuccess(true);
			response.setMessage("Compra actualizada exitosamente");
			response.setData(updatePurchase);
			response.setCode(201);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al actualizar la venta: " + e);
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Eliminar la compra con los detalees
	@DeleteMapping({ "/purchase/delete/{id}" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Purchase> deletePurchasesWithDetails(@PathVariable long id) {

		ApiResponse<Purchase> response = new ApiResponse<>();

		Purchase purchase = iPurchase.findById(id);

		try {

			if (purchase != null) {
				
				List<Purchase_Detail> purchaseDetails = iPurchase.listPurchasesDetailsById(id);

				for (Purchase_Detail purchase_Detail : purchaseDetails) {
					iInventory.deleteInventoryByPurchase(purchase_Detail.getId());
				}

				iPurchase.detetePurchaseDetails(id);
				iPurchase.deletePurchase(purchase);
				response.setSuccess(true);
				response.setMessage("Compra eliminada exitosamente");
				response.setData(null);
				response.setCode(200);

			} else {

				response.setSuccess(false);
				response.setMessage("No se encontró la Compra con el ID proporcionado");
				response.setData(null);
				response.setCode(404);

			}

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error al eliminar la compra: " + e);
			response.setData(null);
			response.setCode(500);

		}

		return response;

	}
}
