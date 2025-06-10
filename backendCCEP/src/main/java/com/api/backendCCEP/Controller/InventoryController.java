package com.api.backendCCEP.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.backendCCEP.Facade.IInventory;
import com.api.backendCCEP.Model.Inventory;
import com.api.backendCCEP.Util.ApiResponse;

@RestController
@RequestMapping({ "/admin" })
public class InventoryController {

	// Instacias
	private IInventory iInventory;	
	
	public InventoryController(IInventory iInventory) {
		this.iInventory = iInventory;
	}

	@GetMapping("/inventories")
	private ApiResponse<Page<Inventory>> getStock(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		
		ApiResponse<Page<Inventory>> response = new ApiResponse<>();
		
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<Inventory> stock = iInventory.stock(pageable);

			response.setSuccess(true);
			response.setMessage("Consulta exitosa");
			response.setData(stock);
			response.setCode(200);

		} catch (Exception e) {

			response.setSuccess(false);
			response.setMessage("Error en la consulta: " + e);
			response.setData(null);
			response.setCode(500);

		}		
		
		return response;
	}
	
}
