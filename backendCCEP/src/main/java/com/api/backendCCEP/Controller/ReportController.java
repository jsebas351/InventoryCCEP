package com.api.backendCCEP.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.backendCCEP.Facade.IReport;
import com.api.backendCCEP.Util.ApiResponse;

@RestController
@RequestMapping({ "/admin" })
public class ReportController {

	// Instancias
	private IReport iReport;
	
	public ReportController(IReport iReport) {
		this.iReport = iReport;
	}

	// Resumen de las ventas filtradas por fecha
	@GetMapping({ "/report/summary" })
	@PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
	public ApiResponse<List<Map<String, Object>>> getSummarySales(@RequestParam String startDate,
			@RequestParam String endDate) {
		ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>();

		try {
			List<Map<String, Object>> summary = iReport.getSalesSummary(startDate, endDate);

			if (summary != null && !summary.isEmpty()) {
				response.setSuccess(true);
				response.setMessage("Consulta Exitosa");
				response.setData(summary);
				response.setCode(200);
			} else {
				response.setSuccess(false);
				response.setMessage("No se encontró un resumen de ventas");
				response.setData(null);
				response.setCode(404);
			}

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error en la consulta");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

}
