package com.api.backendCCEP.Controller;

import java.util.Date;
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

import com.api.backendCCEP.Facade.IEntry;
import com.api.backendCCEP.Facade.IInventory;
import com.api.backendCCEP.Model.Entry;
import com.api.backendCCEP.Model.Inventory;
import com.api.backendCCEP.Util.ApiResponse;

@RestController
@RequestMapping({ "/admin" })
public class EntryController {

	private IEntry iEntry;
	private IInventory iInventory;

	public EntryController(IEntry iEntry, IInventory iInventory) {
		this.iEntry = iEntry;
		this.iInventory = iInventory;
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

	// Listar entradas con paginacion
	@GetMapping({ "/entries" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Page<Entry>> getEntriesList(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size) {

		ApiResponse<Page<Entry>> response = new ApiResponse<>();

		try {
			Pageable pageable = PageRequest.of(page, size);

			Page<Entry> entriesPage = iEntry.entriesListPaginted(pageable);

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(entriesPage);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta: " + e);
			response.setData(null);
			response.setCode(500);

		}
		return response;
	}

	// Creacion de nuevas entradas
	@PostMapping({ "/entries/create" })
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Entry> createEntries(@RequestBody Entry entry) {

		ApiResponse<Entry> response = new ApiResponse<>();

		// Array de campos
		Object[] fieldsToValidate = { entry.getProduct_id(), entry.getQuantity() };

		try {
			// Validar campos obligatorios
			if (isNullOrEmpty(fieldsToValidate)) {
				response.setSuccess(false);
				response.setMessage("Todos los campos son requeridos");
				response.setData(null);
				response.setCode(400);
			} else {

				entry.setDateEntry(new Date());
				iEntry.save(entry);
				
				Inventory inventory = new Inventory();

				inventory.setEntry_id(entry);
				inventory.setProduct_id(entry.getProduct_id());
				inventory.setStock(entry.getQuantity());
				
				iInventory.save(inventory);
				
				response.setSuccess(true);
				response.setMessage("Entrada registrada exitosamente");
				response.setData(null);
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

	// Actualizar Entradas
	@PutMapping("/entries/update/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<Entry> updateEntry(@PathVariable long id, @RequestBody Entry updatedEntry) {
		ApiResponse<Entry> response = new ApiResponse<>();

		try {

			// Actualizar la Entrada solo si existe
			Optional<Entry> optionalEntry = Optional.of(iEntry.findById(id));

			if (optionalEntry.isPresent()) {
				Entry existingEntry = optionalEntry.get();

				// Actualizar los campos de la entrada existente con la información
				// proporcionada
				existingEntry.setProduct_id(updatedEntry.getProduct_id());
				existingEntry.setQuantity(updatedEntry.getQuantity());

				// Guardar la fecha de edicion
				existingEntry.setEdit_date(new Date());

				// Guardar la Entrada actualizada
				iEntry.save(existingEntry);

				// Encontrar el inventario basado en el id de la Entrada
				Inventory existingInventory = iInventory.findInvetoryByEntry(id).orElse(null);

				existingInventory.setProduct_id(existingEntry.getProduct_id());
				existingInventory.setStock(existingEntry.getQuantity());
				
				// Guardar el inventario Actualizado
				iInventory.save(existingInventory);
				
				response.setSuccess(true);
				response.setMessage("Entrada actualizada exitosamente");
				response.setData(existingEntry);
				response.setCode(200);
			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró la entrada con el ID proporcionado");
				response.setData(null);
				response.setCode(404);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al actualizar la entrada");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Eliminar la entrada
	@DeleteMapping("/entries/delete/{id}")
	@PreAuthorize("hasRole('Administrador')")
	public ApiResponse<String> deleteEntries(@PathVariable Long id) {

		ApiResponse<String> response = new ApiResponse<>();

		try {
			// Verificar si la entrada existe antes de intentar eliminarla
			Entry existingEntry = iEntry.findById(id);

			if (existingEntry != null) {

				// Eliminar primero el inventario con el id de la entrada
				iInventory.deleteInventoryByEntry(id);
				
				// Eliminar la entrada
				iEntry.delete(existingEntry);

				response.setSuccess(true);
				response.setMessage("Entrada eliminada exitosamente");
				response.setData(null);
				response.setCode(200);

			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró la Entrada con el ID proporcionado");
				response.setData(null);
				response.setCode(404);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al eliminar la Entrada");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

}
