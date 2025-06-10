package com.api.backendCCEP.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.backendCCEP.Facade.IFilesUpload;
import com.api.backendCCEP.Model.Category;
import com.api.backendCCEP.Model.Product;
import com.api.backendCCEP.Model.SubCategory;
import com.api.backendCCEP.Model.Supplier;
import com.api.backendCCEP.Util.ApiResponse;
import com.api.backendCCEP.Validations.FileValidator;

@RestController
@RequestMapping({ "/admin" })
public class FilesUploadController {

	private IFilesUpload iFilesUpload;

	public FilesUploadController(IFilesUpload iFilesUpload) {
		this.iFilesUpload = iFilesUpload;
	}

	// Guardar las Categorias
	@PostMapping("/file/saveCategories")
	public ApiResponse<Category> saveCategoriesExcel(@RequestParam("excel") MultipartFile file) {

		ApiResponse<Category> response = new ApiResponse<>();

		try {

			// Validar si el archivo está vacío
			if (FileValidator.isFileEmpty(file)) {
				response.setMessage("El archivo está vacío");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es demasiado grande (por ejemplo, 5MB)
			if (FileValidator.isFileTooLarge(file, 5000000)) {
				response.setMessage("El archivo es demasiado grande");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es un archivo Excel
			if (!FileValidator.isExcelFile(file)) {
				response.setMessage("El archivo no es un archivo Excel");
				response.setSuccess(false);
				response.setCode(415);
				return response;
			}

			// Validar si el archivo Excel es válido (no está dañado)
			if (!FileValidator.isExcelFileValid(file)) {
				response.setMessage("El archivo Excel está dañado o tiene un formato incorrecto");
				response.setSuccess(false);
				response.setCode(422);
				return response;
			}

			// Enviar el archivo al servicio para procesarlo
			this.iFilesUpload.saveCategoriesExcel(file);

			response.setSuccess(true);
			response.setMessage("Se guardaron las categorias Correctamente");
			response.setData(null);
			response.setCode(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al procesar el archivo");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Guardar las SubCategorias
	@PostMapping("/file/saveSubCategories")
	public ApiResponse<SubCategory> saveSubCategoriesExcel(@RequestParam("excel") MultipartFile file) {

		ApiResponse<SubCategory> response = new ApiResponse<>();

		try {

			// Validar si el archivo está vacío
			if (FileValidator.isFileEmpty(file)) {
				response.setMessage("El archivo está vacío");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es demasiado grande (por ejemplo, 5MB)
			if (FileValidator.isFileTooLarge(file, 5000000)) {
				response.setMessage("El archivo es demasiado grande");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es un archivo Excel
			if (!FileValidator.isExcelFile(file)) {
				response.setMessage("El archivo no es un archivo Excel");
				response.setSuccess(false);
				response.setCode(415);
				return response;
			}

			// Validar si el archivo Excel es válido (no está dañado)
			if (!FileValidator.isExcelFileValid(file)) {
				response.setMessage("El archivo Excel está dañado o tiene un formato incorrecto");
				response.setSuccess(false);
				response.setCode(422);
				return response;
			}

			// Enviar el archivo al servicio para procesarlo
			this.iFilesUpload.saveSubCategoriesExcel(file);

			response.setSuccess(true);
			response.setMessage("Se guardaron las SubCategorias Correctamente");
			response.setData(null);
			response.setCode(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al procesar el archivo");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Guardar los Proveedores
	@PostMapping("/file/saveSuppliers")
	public ApiResponse<Supplier> saveSuppliersExcel(@RequestParam("excel") MultipartFile file) {

		ApiResponse<Supplier> response = new ApiResponse<>();

		try {

			// Validar si el archivo está vacío
			if (FileValidator.isFileEmpty(file)) {
				response.setMessage("El archivo está vacío");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es demasiado grande (por ejemplo, 5MB)
			if (FileValidator.isFileTooLarge(file, 5000000)) {
				response.setMessage("El archivo es demasiado grande");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es un archivo Excel
			if (!FileValidator.isExcelFile(file)) {
				response.setMessage("El archivo no es un archivo Excel");
				response.setSuccess(false);
				response.setCode(415);
				return response;
			}

			// Validar si el archivo Excel es válido (no está dañado)
			if (!FileValidator.isExcelFileValid(file)) {
				response.setMessage("El archivo Excel está dañado o tiene un formato incorrecto");
				response.setSuccess(false);
				response.setCode(422);
				return response;
			}

			// Enviar el archivo al servicio para procesarlo
			this.iFilesUpload.saveSuppliersExcel(file);

			response.setSuccess(true);
			response.setMessage("Se guardaron los Proveedores Correctamente");
			response.setData(null);
			response.setCode(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al procesar el archivo");
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Guardar los productos
	@PostMapping("/file/saveProducts")
	public ApiResponse<Product> saveProductsExcel(@RequestParam("excel") MultipartFile file) {

		ApiResponse<Product> response = new ApiResponse<>();

		try {

			// Validar si el archivo está vacío
			if (FileValidator.isFileEmpty(file)) {
				response.setMessage("El archivo está vacío");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es demasiado grande (por ejemplo, 5MB)
			if (FileValidator.isFileTooLarge(file, 5000000)) {
				response.setMessage("El archivo es demasiado grande");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es un archivo Excel
			if (!FileValidator.isExcelFile(file)) {
				response.setMessage("El archivo no es un archivo Excel");
				response.setSuccess(false);
				response.setCode(415);
				return response;
			}

			// Validar si el archivo Excel es válido (no está dañado)
			if (!FileValidator.isExcelFileValid(file)) {
				response.setMessage("El archivo Excel está dañado o tiene un formato incorrecto");
				response.setSuccess(false);
				response.setCode(422);
				return response;
			}

			// Enviar el archivo al servicio para procesarlo
			this.iFilesUpload.saveProductsExcel(file);

			response.setSuccess(true);
			response.setMessage("Se guardaron los Productos Correctamente");
			response.setData(null);
			response.setCode(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al procesar el archivo: " + e);
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

	// Guardar las Entradas
	@PostMapping("/file/saveEntries")
	public ApiResponse<Product> saveEntriesExcel(@RequestParam("excel") MultipartFile file) {

		ApiResponse<Product> response = new ApiResponse<>();

		try {

			// Validar si el archivo está vacío
			if (FileValidator.isFileEmpty(file)) {
				response.setMessage("El archivo está vacío");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es demasiado grande (por ejemplo, 5MB)
			if (FileValidator.isFileTooLarge(file, 5000000)) {
				response.setMessage("El archivo es demasiado grande");
				response.setSuccess(false);
				response.setCode(400);
				return response;
			}

			// Validar si el archivo es un archivo Excel
			if (!FileValidator.isExcelFile(file)) {
				response.setMessage("El archivo no es un archivo Excel");
				response.setSuccess(false);
				response.setCode(415);
				return response;
			}

			// Validar si el archivo Excel es válido (no está dañado)
			if (!FileValidator.isExcelFileValid(file)) {
				response.setMessage("El archivo Excel está dañado o tiene un formato incorrecto");
				response.setSuccess(false);
				response.setCode(422);
				return response;
			}

			// Enviar el archivo al servicio para procesarlo
			this.iFilesUpload.saveEntriesExcel(file);

			response.setSuccess(true);
			response.setMessage("Se registraron las Entradas Correctamente");
			response.setData(null);
			response.setCode(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error al procesar el archivo: " + e);
			response.setData(null);
			response.setCode(500);
		}

		return response;
	}

}
