package com.api.backendCCEP.FacadeImp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.backendCCEP.Facade.IFilesUpload;
import com.api.backendCCEP.Model.Category;
import com.api.backendCCEP.Model.Entry;
import com.api.backendCCEP.Model.Inventory;
import com.api.backendCCEP.Model.Product;
import com.api.backendCCEP.Model.SubCategory;
import com.api.backendCCEP.Model.Supplier;
import com.api.backendCCEP.Repository.CategoryRepository;
import com.api.backendCCEP.Repository.EntryRepository;
import com.api.backendCCEP.Repository.InventoryRepository;
import com.api.backendCCEP.Repository.ProductRepository;
import com.api.backendCCEP.Repository.SubCategoryRepository;
import com.api.backendCCEP.Repository.SupplierRepository;

@Service
public class FilesUploadService implements IFilesUpload {

	private CategoryRepository categoryRepository;
	private SubCategoryRepository subCategoryRepository;
	private SupplierRepository supplierRepository;
	private ProductRepository productRepository;
	private EntryRepository entryRepository;
	private InventoryRepository inventoryRepository;

	public FilesUploadService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository,
			SupplierRepository supplierRepository, ProductRepository productRepository,
			EntryRepository entryRepository, InventoryRepository inventoryRepository) {
		this.categoryRepository = categoryRepository;
		this.subCategoryRepository = subCategoryRepository;
		this.supplierRepository = supplierRepository;
		this.productRepository = productRepository;
		this.entryRepository = entryRepository;
		this.inventoryRepository = inventoryRepository;
	}

	private final Path rootFolder = Paths.get("uploads");

	@Override
	public void saveFile(MultipartFile file) throws IOException {
		Files.copy(file.getInputStream(), this.rootFolder.resolve(file.getOriginalFilename()));
	}

	// Guardar Categorias
	@Override
	@Secured("ROLE_Administrador")
	public void saveCategoriesExcel(MultipartFile file) throws Exception {

		this.saveFile(file);

		String nameFile = "uploads/" + file.getOriginalFilename();
		File uploadedFile = new File(nameFile);

		// Procesar el archivo Excel
		try (FileInputStream fileInput = new FileInputStream(new File(nameFile));
				XSSFWorkbook book = new XSSFWorkbook(fileInput)) {

			XSSFSheet sheet = book.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			boolean isFirstRow = true;

			// Iterar sobre las filas del archivo
			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();

				// Saltar la primera fila
				if (isFirstRow) {
					isFirstRow = false;
					continue;
				}

				Iterator<Cell> cellIterator = row.cellIterator();

				// Verificar si hay celdas suficientes
				if (!cellIterator.hasNext())
					continue;

				Cell cell = cellIterator.next();

				Category category = new Category();

				// Asignar el nombre y el estado
				category.setName(cell.getStringCellValue());
				category.setState("Activo");

				this.categoryRepository.save(category);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Error al procesar el archivo Excel: " + e.getMessage());
		} finally {
			// Eliminar el archivo después de procesarlo
			try {
				if (uploadedFile.exists()) {
					Files.delete(uploadedFile.toPath());
					System.out.println("Archivo eliminado: " + uploadedFile.getAbsolutePath());
				}
			} catch (IOException e) {
				System.err.println("No se pudo eliminar el archivo: " + uploadedFile.getAbsolutePath());
			}
		}

	}

	// Guardar SubCategorias
	@Override
	@Secured("ROLE_Administrador")
	public void saveSubCategoriesExcel(MultipartFile file) throws Exception {

	    this.saveFile(file);

	    String nameFile = "uploads/" + file.getOriginalFilename();
	    File uploadedFile = new File(nameFile);

	    // Procesar el archivo Excel
	    try (FileInputStream fileInput = new FileInputStream(new File(nameFile));
	         XSSFWorkbook book = new XSSFWorkbook(fileInput)) {

	        XSSFSheet sheet = book.getSheetAt(0);

	        Iterator<Row> rowIterator = sheet.iterator();

	        boolean isFirstRow = true;

	        // Iterar sobre las filas del archivo
	        while (rowIterator.hasNext()) {

	            Row row = rowIterator.next();

	            // Saltar la primera fila (encabezado)
	            if (isFirstRow) {
	                isFirstRow = false;
	                continue;
	            }

	            // Verificar si la fila está completamente vacía
	            if (row == null || isRowEmpty(row)) {
	                continue;
	            }

	            try {
	                Iterator<Cell> cellIterator = row.cellIterator();

	                // Verificar si hay celdas suficientes
	                if (!cellIterator.hasNext()) {
	                    continue;
	                }

	                Cell cell = cellIterator.next();

	                SubCategory subCategory = new SubCategory();

	                subCategory.setName(cell.getStringCellValue());

	                // Verificar si hay más celdas
	                if (cellIterator.hasNext()) {
	                    cell = cellIterator.next();

	                    // Encontrar la Categoria por el nombre exacto
	                    Category category = categoryRepository.findByName(cell.getStringCellValue()).orElse(null);

	                    if (category == null) {
	                        throw new IllegalArgumentException("Categoría no encontrada: " + cell.getStringCellValue());
	                    }

	                    subCategory.setCategory_id(category);
	                }

	                subCategory.setState("Activo");

	                // Guardar la subcategoría
	                this.subCategoryRepository.save(subCategory);

	            } catch (Exception e) {
	                // Registrar el error específico por cada fila
	                System.err.println("Error procesando fila: " + row.getRowNum() + ". " + e.getMessage());
	            }
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new Exception("Error al procesar el archivo Excel: " + e.getMessage());
	    } finally {
	        // Eliminar el archivo después de procesarlo
	        try {
	            if (uploadedFile.exists()) {
	                Files.delete(uploadedFile.toPath());
	                System.out.println("Archivo eliminado: " + uploadedFile.getAbsolutePath());
	            }
	        } catch (IOException e) {
	            System.err.println("No se pudo eliminar el archivo: " + uploadedFile.getAbsolutePath());
	            e.printStackTrace();
	        }
	    }
	}

	// Guardar Proveedores
	@Override
	@Secured("ROLE_Administrador")
	public void saveSuppliersExcel(MultipartFile file) throws Exception {
	    this.saveFile(file);

	    String nameFile = "uploads/" + file.getOriginalFilename();
	    File uploadedFile = new File(nameFile);

	    // Procesar el archivo Excel
	    try (FileInputStream fileInput = new FileInputStream(new File(nameFile));
	         XSSFWorkbook book = new XSSFWorkbook(fileInput)) {

	        XSSFSheet sheet = book.getSheetAt(0);

	        Iterator<Row> rowIterator = sheet.iterator();

	        boolean isFirstRow = true;

	        // Iterar sobre las filas del archivo
	        while (rowIterator.hasNext()) {

	            Row row = rowIterator.next();

	            // Saltar la primera fila (encabezado)
	            if (isFirstRow) {
	                isFirstRow = false;
	                continue;
	            }

	            // Verificar si la fila está completamente vacía
	            if (row == null || isRowEmpty(row)) {
	                continue;
	            }

	            try {
	                Supplier supplier = new Supplier();
	                Iterator<Cell> cellIterator = row.cellIterator();

	                // Verificar si hay celdas suficientes
	                if (!cellIterator.hasNext()) {
	                    continue;
	                }

	                // Leer celdas y asignar valores
	                Cell cell = cellIterator.next();
	                supplier.setNit((long) cell.getNumericCellValue()); // NIT

	                if (cellIterator.hasNext()) {
	                    cell = cellIterator.next();
	                    supplier.setName(cell.getStringCellValue()); // Nombre
	                }

	                if (cellIterator.hasNext()) {
	                    cell = cellIterator.next();
	                    supplier.setPhone((long) cell.getNumericCellValue()); // Teléfono
	                }

	                if (cellIterator.hasNext()) {
	                    cell = cellIterator.next();
	                    supplier.setMail(cell.getStringCellValue()); // Correo
	                }

	                // Estado
	                supplier.setState("Activo");

	                // Guardar proveedor
	                this.supplierRepository.save(supplier);

	            } catch (Exception e) {
	                System.err.println("Error procesando fila: " + row.getRowNum() + ". " + e.getMessage());
	            }
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new Exception("Error al procesar el archivo Excel: " + e.getMessage());
	    } finally {
	        try {
	            if (uploadedFile.exists()) {
	                Files.delete(uploadedFile.toPath());
	                System.out.println("Archivo eliminado: " + uploadedFile.getAbsolutePath());
	            }
	        } catch (IOException e) {
	            System.err.println("No se pudo eliminar el archivo: " + uploadedFile.getAbsolutePath());
	            e.printStackTrace();
	        }
	    }
	}

	@Override
	@Secured("ROLE_Administrador")
	public void saveProductsExcel(MultipartFile file) throws Exception {

		this.saveFile(file);

		String nameFile = "uploads/" + file.getOriginalFilename();
		File uploadedFile = new File(nameFile);

		try (FileInputStream fileInput = new FileInputStream(uploadedFile);
				XSSFWorkbook book = new XSSFWorkbook(fileInput)) {

			XSSFSheet sheet = book.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			boolean isFirstRow = true;

			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();

				// Saltar la primera fila (encabezado)
				if (isFirstRow) {
					isFirstRow = false;
					continue;
				}

				// Verificar si la fila está completamente vacía
				if (row == null || isRowEmpty(row)) {
					continue;
				}

				try {
					Product product = new Product();
					Iterator<Cell> cellIterator = row.cellIterator();

					if (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						product.setName(getStringCellValue(cell));
					}

					if (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						product.setDescription(getStringCellValue(cell));
					}

					if (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						product.setPurchase_price(getLongCellValue(cell));
					}

					if (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						product.setSale_price(getLongCellValue(cell));
					}

					if (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						SubCategory subCategory = subCategoryRepository.findByName(getStringCellValue(cell))
								.orElseThrow(() -> new IllegalArgumentException("Subcategoría no encontrada"));
						product.setSubcategory_id(subCategory);
					}

					if (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						Supplier supplier = supplierRepository.findByName(getStringCellValue(cell))
								.orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
						product.setProvider_id(supplier);
					}

					product.setState("Activo");

					// Generar referencia automática
					Long salePrice = product.getSale_price();
					String seconds = String.format("%02d", LocalDateTime.now().getSecond());
					String minutes = String.format("%02d", LocalDateTime.now().getMinute());
					String firstTwoDigits = salePrice.toString().substring(0,
							Math.min(2, salePrice.toString().length()));
					String reference = String.format("%02d%s%s%s", product.getSubcategory_id().getId(),
							product.getProvider_id().getId(), firstTwoDigits, seconds + minutes);
					product.setReference(Long.parseLong(reference));

					this.productRepository.save(product);

				} catch (Exception e) {
					System.err.println("Error procesando fila: " + row.getRowNum() + ". " + e.getMessage());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Error al procesar el archivo Excel: " + e.getMessage());
		} finally {
			try {
				if (uploadedFile.exists()) {
					Files.delete(uploadedFile.toPath());
					System.out.println("Archivo eliminado: " + uploadedFile.getAbsolutePath());
				}
			} catch (IOException e) {
				System.err.println("No se pudo eliminar el archivo: " + uploadedFile.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void saveEntriesExcel(MultipartFile file) throws Exception {
		
		this.saveFile(file);

	    String nameFile = "uploads/" + file.getOriginalFilename();
	    File uploadedFile = new File(nameFile);

	    // Procesar el archivo Excel
	    try (FileInputStream fileInput = new FileInputStream(new File(nameFile));
	         XSSFWorkbook book = new XSSFWorkbook(fileInput)) {

	        XSSFSheet sheet = book.getSheetAt(0);

	        Iterator<Row> rowIterator = sheet.iterator();

	        boolean isFirstRow = true;

	        // Iterar sobre las filas del archivo
	        while (rowIterator.hasNext()) {

	            Row row = rowIterator.next();

	            // Saltar la primera fila (encabezado)
	            if (isFirstRow) {
	                isFirstRow = false;
	                continue;
	            }

	            // Verificar si la fila está completamente vacía
	            if (row == null || isRowEmpty(row)) {
	                continue;
	            }

	            try {
	                Entry entry = new Entry();
	                Inventory inventory = new Inventory();
	                Iterator<Cell> cellIterator = row.cellIterator();

	                // Verificar si hay celdas suficientes
	                if (!cellIterator.hasNext()) {
	                    continue;
	                }

	                // Leer celdas y asignar valores
	                if (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						Product product = productRepository.findByName(getStringCellValue(cell))
								.orElseThrow(() -> new IllegalArgumentException("Producto no entonctrado"));
						entry.setProduct_id(product); // Producto
						inventory.setProduct_id(product);						
					}

	                if (cellIterator.hasNext()) {
	                    Cell cell = cellIterator.next();
	                    entry.setQuantity(getLongCellValue(cell)); // Cantidad
	                    inventory.setStock(getLongCellValue(cell));
	                }
	                
	                //Registrar la fecha de ingreso
	                entry.setDateEntry(new Date());

	                // Guardar proveedor
	                this.entryRepository.save(entry);
	                
	                // Asignar el inventario con el id de la entrada
	                inventory.setEntry_id(entry);
	                
	                // Guardar inventario 
	                this.inventoryRepository.save(inventory);

	            } catch (Exception e) {
	                System.err.println("Error procesando fila: " + row.getRowNum() + ". " + e.getMessage());
	            }
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new Exception("Error al procesar el archivo Excel: " + e.getMessage());
	    } finally {
	        try {
	            if (uploadedFile.exists()) {
	                Files.delete(uploadedFile.toPath());
	                System.out.println("Archivo eliminado: " + uploadedFile.getAbsolutePath());
	            }
	        } catch (IOException e) {
	            System.err.println("No se pudo eliminar el archivo: " + uploadedFile.getAbsolutePath());
	            e.printStackTrace();
	        }
	    }
		
	}

	// Método para verificar si una fila está vacía
	private boolean isRowEmpty(Row row) {
		for (Cell cell : row) {
			if (cell != null && cell.getCellType() != CellType.BLANK) {
				return false;
			}
		}
		return true;
	}

	// Obtener valor de celda como String
	private String getStringCellValue(Cell cell) {
		if (cell.getCellType() == CellType.STRING) {
			return cell.getStringCellValue();
		} else if (cell.getCellType() == CellType.NUMERIC) {
			return String.valueOf((long) cell.getNumericCellValue());
		}
		return "";
	}

	// Obtener valor de celda como Long
	private Long getLongCellValue(Cell cell) {
		if (cell.getCellType() == CellType.NUMERIC) {
			return (long) cell.getNumericCellValue();
		} else if (cell.getCellType() == CellType.STRING) {
			return Long.valueOf(cell.getStringCellValue());
		}
		return 0L;
	}

}
