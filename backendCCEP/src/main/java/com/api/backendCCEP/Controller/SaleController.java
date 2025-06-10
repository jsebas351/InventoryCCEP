package com.api.backendCCEP.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.backendCCEP.Facade.IInventory;
import com.api.backendCCEP.Facade.IPaymentMethod;
import com.api.backendCCEP.Facade.IProduct;
import com.api.backendCCEP.Facade.ISale;
import com.api.backendCCEP.Model.Inventory;
import com.api.backendCCEP.Model.Payment_Method;
import com.api.backendCCEP.Model.Product;
import com.api.backendCCEP.Model.Sale;
import com.api.backendCCEP.Model.Sale_Detail;
import com.api.backendCCEP.Util.ApiResponse;

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

@RestController
@RequestMapping({ "/vendor" })
public class SaleController {

	// Instacias
	private IProduct iProduct;
	private ISale iSale;
	private IPaymentMethod iPaymentMethod;
	private IInventory iInventory;

	public SaleController(IProduct iProduct, ISale iSale, IPaymentMethod iPaymentMethod, IInventory iInventory) {
		this.iProduct = iProduct;
		this.iSale = iSale;
		this.iPaymentMethod = iPaymentMethod;
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

	// Listar Ventas
	@GetMapping({ "/sales" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Page<Sale>> getSalesListPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		ApiResponse<Page<Sale>> response = new ApiResponse<>();

		try {
			Pageable pageable = PageRequest.of(page, size);

			Page<Sale> sales = iSale.listSales(pageable);

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(sales);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Obtener las ventas registradas por el id
	@GetMapping({ "/sales/{id}" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Sale> getSalesById(@PathVariable int id) {

		ApiResponse<Sale> response = new ApiResponse<>();

		try {

			Sale sale = iSale.findById(id);

			if (sale == null) {
				response.setSuccess(false);
				response.setMessage("No se encontro la venta");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			response.setSuccess(true);
			response.setMessage("Consulta Exitosa");
			response.setData(sale);
			response.setCode(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Filtrar los detalles basado en el id de la venta
	@GetMapping("/sales/detailsbyid/{saleId}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<List<Sale_Detail>> getSaleDetailsById(@PathVariable int saleId) {

		ApiResponse<List<Sale_Detail>> response = new ApiResponse<>();

		try {

			List<Sale_Detail> salesDetails = iSale.listSaleDetailsById(saleId);

			// Encontrar la venta por el id
			Sale saleUpdate = iSale.findById(saleId);

			// Si no se encuentra la venta mostrar la validacion
			if (saleUpdate == null) {
				response.setSuccess(false);
				response.setMessage("No se encontro la venta");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			response.setSuccess(true);
			response.setMessage("Consulta Exitosa");
			response.setData(salesDetails);
			response.setCode(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Listar los metodos de pago
	@GetMapping("/paymentmethod")
	@PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
	public ApiResponse<List<Payment_Method>> getPaymentsMethods() {

		ApiResponse<List<Payment_Method>> response = new ApiResponse<>();

		try {
			List<Payment_Method> payment_Methods = iPaymentMethod.allPaymentMethods();

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(payment_Methods);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);

		}

		return response;
	}

	// Registrar Ventas
	@PostMapping("/sales/create")
	@PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
	public ApiResponse<Sale> saveSaleWithDetails(@RequestBody Map<String, Object> request) {

		ApiResponse<Sale> response = new ApiResponse<>();

		try {

			Object[] fieldsToValidateSale = { request.get("user_id"), request.get("paymethod_id"),
					request.get("discount") };

			// Validar que el campo user_id y discount no esten vacíos
			if (isNullOrEmpty(fieldsToValidateSale)) {
				response.setSuccess(false);
				response.setMessage(
						"El campo 'usuario', 'Metodo de pago' y 'descuento' es obligatorio y no puede estar vacío.");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			// Cargar completamente el metodo de pago antes de asignarlo a la venta
			Payment_Method payment_Method = iPaymentMethod.findById((Integer) request.get("paymethod_id"));
			if (payment_Method == null) {
				response.setSuccess(false);
				response.setMessage("El metodo de pago no existe");
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
				Object[] fieldsToValidateDetail = { detailMap.get("quantity"), detailMap.get("product_id"),
						detailMap.get("discount_product") };

				// Validar que los campos de cada detalle no estén vacíos
				if (isNullOrEmpty(fieldsToValidateDetail)) {
					response.setSuccess(false);
					response.setMessage(
							"Los campos 'Cantidad', 'Producto' y Descuento Unitario son obligatorios y no pueden estar vacíos");
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
			long discount = (Integer) request.get("discount");

			// Instaciar el objeto sale
			Sale sale = new Sale();

			// Llenar los campos de la venta
			sale.setSale_date(new Date());
			sale.setTotal_sale(total);
			sale.setDiscount(0);
			sale.setUser_id((Integer) request.get("user_id"));
			sale.setPaymethod_id(payment_Method);
			sale.setState("Activo");
			iSale.save(sale);

			// Recorrer el objeto detalles, para llenar los campos de los detalles
			for (Map<String, Object> detailMap : details) {

				// Inicializar la variable del subtotal
				long subtotal = 0;

				// Instaciar el objeto Sale_Details
				Sale_Detail detail = new Sale_Detail();

				// Cargar completamente el producto antes de asignarlo al detalle
				Product product = iProduct.findById((Integer) detailMap.get("product_id"));

				// Definir las variables para calcular el subtotal
				long salePrice = product.getSale_price();

				// Definir el campo cantidad
				Integer quantity = (Integer) detailMap.get("quantity");

				// Definir el descuento unitario
				long discount_product = (Integer) detailMap.get("discount_product");

				// Calcular el subtotal antes de aplicar el descuento
				subtotal = ((Integer) detailMap.get("quantity")) * salePrice;

				// Calcular el valor del subtotal con el descuento unitario
				subtotal -= discount_product;

				// Si el subtotal es negativo debido al descuento, establecerlo como cero
				subtotal = Math.max(subtotal, 0);

				// Agregar el subtotal al total de la venta
				total += subtotal;

				// Llenar los campos
				detail.setSale_id(sale);
				detail.setQuantity(quantity);
				detail.setProduct_id(product);
				detail.setDiscount_product(discount_product);
				detail.setSubtotal(subtotal);
				iSale.saveDetails(detail);

				Inventory inventory = new Inventory();

				inventory.setProduct_id(product);
				inventory.setSaledetail_id(detail);
				inventory.setStock(-quantity);
				iInventory.save(inventory);
			}

			total -= discount;

			// Si el total es negativo después de aplicar el descuento total, establecerlo
			// como cero
			total = Math.max(total, 0);

			sale.setDiscount(discount);
			sale.setTotal_sale(total);
			iSale.save(sale);

			response.setSuccess(true);
			response.setMessage("Venta Guardada exitosamente");
			response.setData(sale);
			response.setCode(201);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al guardar la venta");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Actualizar Ventas
	@PutMapping("/sales/update/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Sale> updateSaleWithDetails(@PathVariable("id") int id,
			@RequestBody Map<String, Object> request) {

		ApiResponse<Sale> response = new ApiResponse<>();

		try {

			// Buscar la venta existente
			Optional<Sale> optionalSale = Optional.of(iSale.findById(id));
			if (!optionalSale.isPresent()) {
				response.setSuccess(false);
				response.setMessage("La venta con el ID proporcionado no se encuentra.");
				response.setData(null);
				response.setCode(404);
				return response;
			}

			Object[] fieldsToValidateSale = { request.get("user_id"), request.get("paymethod_id"),
					request.get("discount") };

			// Validar que el campo user_id y discount no esten vacíos
			if (isNullOrEmpty(fieldsToValidateSale)) {
				response.setSuccess(false);
				response.setMessage(
						"El campo 'usuario', 'Metodo de pago' y 'descuento' es obligatorio y no puede estar vacío.");
				response.setData(null);
				response.setCode(400);
				return response;
			}

			// Cargar completamente el metodo de pago antes de asignarlo a la venta
			Payment_Method payment_Method = iPaymentMethod.findById((Integer) request.get("paymethod_id"));
			if (payment_Method == null) {
				response.setSuccess(false);
				response.setMessage("El metodo de pago no existe");
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
				Object[] fieldsToValidateDetail = { detailMap.get("quantity"), detailMap.get("product_id"),
						detailMap.get("discount_product") };

				// Validar que los campos de cada detalle no estén vacíos
				if (isNullOrEmpty(fieldsToValidateDetail)) {
					response.setSuccess(false);
					response.setMessage(
							"Los campos 'Cantidad', 'Producto' y Descuento Unitario son obligatorios y no pueden estar vacíos");
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
			long discount = (Integer) request.get("discount");

			// Instaciar el objeto saleUpdate
			Sale saleUpdate = optionalSale.get();

			// Llenar los campos de la venta
			saleUpdate.setEdit_date(new Date());
			saleUpdate.setTotal_sale(total);
			saleUpdate.setDiscount(0);
			saleUpdate.setUser_id((Integer) request.get("user_id"));
			saleUpdate.setPaymethod_id(payment_Method);
			saleUpdate.setState((String) request.get("status"));
			iSale.save(saleUpdate);

			// Obtener los detalles existentes de la venta que se va a actualizar
			List<Sale_Detail> existingDetails = iSale.listSaleDetailsById(id);

			// Lista para almacenar los nuevos detalles que se agregarán
			List<Sale_Detail> newDetails = new ArrayList<>();

			// Recorrer el objeto detalles, para llenar los campos de los detalles
			for (Map<String, Object> detailMap : details) {

				// Obtener el ID del detalle de la solicitud
				Integer detailId = (Integer) detailMap.get("id");

				// Buscar si el detalle existe en los detalles existentes
				Sale_Detail existingDetail = null;
				for (Sale_Detail detail : existingDetails) {
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
				long salePrice = product.getSale_price();

				// Definir el campo cantidad
				Integer quantity = (Integer) detailMap.get("quantity");

				// Definir el descuento unitario
				long discount_product = (Integer) detailMap.get("discount_product");

				// Calcular el subtotal antes de aplicar el descuento
				subtotal = ((Integer) detailMap.get("quantity")) * salePrice;

				// Calcular el valor del subtotal con el descuento unitario
				subtotal -= discount_product;

				// Si el subtotal es negativo debido al descuento, establecerlo como cero
				subtotal = Math.max(subtotal, 0);

				// Agregar el subtotal al total de la venta
				total += subtotal;

				// Si el detalle existe, actualizarlo
				if (existingDetail != null) {

					// Llenar los campos
					existingDetail.setSale_id(saleUpdate);
					existingDetail.setQuantity(quantity);
					existingDetail.setProduct_id(product);
					existingDetail.setDiscount_product(discount_product);
					existingDetail.setSubtotal(subtotal);
					iSale.saveDetails(existingDetail);
					
					//Instacia del inventario
					Inventory existingInventory = iInventory.findInvetoryBySale(existingDetail.getId()).orElse(null);
					
					existingInventory.setProduct_id(product);
					existingInventory.setStock(-quantity);
					iInventory.save(existingInventory);

				} else {
					// Si el detalle no existe, crear uno nuevo y agregarlo a la lista de nuevos
					// detalles
					Sale_Detail newDetail = new Sale_Detail();

					newDetail.setQuantity(quantity);
					newDetail.setProduct_id(product);
					newDetail.setDiscount_product(discount_product);
					newDetail.setSubtotal(subtotal);

					// Agregar el nuevo detalle a la lista de nuevos detalles
					newDetails.add(newDetail);
				}
			}

			// Eliminar los detalles existentes que no están presentes en los nuevos
			// detalles
			for (Sale_Detail detail : existingDetails) {
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
			    	iInventory.deleteInventoryBySale(detail.getId());
			        iSale.deteteSalesDetailsUpdate(detail);
			        
			    }
			}

			// Agregar los nuevos detalles que no existen en la venta
			for (Sale_Detail newDetail : newDetails) {
			    // Asignar la venta a la que pertenecen los nuevos detalles
			    newDetail.setSale_id(saleUpdate);
			    // Guardar los nuevos detalles
			    iSale.saveDetails(newDetail);
			    
			    //Instacia del inventario
				Inventory newInventory = new Inventory();
				
				newInventory.setProduct_id(newDetail.getProduct_id());
				newInventory.setSaledetail_id(newDetail);
				newInventory.setStock(-newDetail.getQuantity());
				iInventory.save(newInventory);
			}

			total -= discount;

			// Si el total es negativo después de aplicar el descuento total, establecerlo
			// como cero
			total = Math.max(total, 0);

			saleUpdate.setDiscount(discount);
			saleUpdate.setTotal_sale(total);
			iSale.save(saleUpdate);

			response.setSuccess(true);
			response.setMessage("Venta actualizada exitosamente");
			response.setData(saleUpdate);
			response.setCode(201);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al actualizar la venta");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Eliminar Ventas con los detalles
	@DeleteMapping("/sales/delete/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Sale> deleteSalesWithDetails(@PathVariable long id) {

		ApiResponse<Sale> response = new ApiResponse<>();

		// Verificar si existe la venta antes de eliminarla
		Sale saleUpdate = iSale.findById(id);

		try {

			if (saleUpdate != null) {

				List<Sale_Detail> salesDetails = iSale.listSaleDetailsById(id);
				
				for (Sale_Detail sale_Detail : salesDetails) {
					iInventory.deleteInventoryBySale(sale_Detail.getId());
				}
				
				iSale.deteteSalesDetails(id);
				iSale.deleteSales(saleUpdate);
				response.setSuccess(true);
				response.setMessage("Venta eliminada exitosamente");
				response.setData(null);
				response.setCode(200);
			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró la Venta con el ID proporcionado");
				response.setData(null);
				response.setCode(404);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al eliminar la venta");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

}
