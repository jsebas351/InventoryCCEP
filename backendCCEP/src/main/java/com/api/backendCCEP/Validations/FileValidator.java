package com.api.backendCCEP.Validations;

import java.io.IOException;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator {

	// Validar si el archivo es un archivo Excel basado en su extensión
    public static boolean isExcelFile(MultipartFile file) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        return fileExtension.equalsIgnoreCase("xls") || fileExtension.equalsIgnoreCase("xlsx");
    }

    // Validar si el archivo está vacío
    public static boolean isFileEmpty(MultipartFile file) {
        return file.isEmpty();
    }

    // Validar si el archivo es demasiado grande
    public static boolean isFileTooLarge(MultipartFile file, long maxSize) {
        return file.getSize() > maxSize;
    }

    // Validar si el archivo Excel es válido (no está dañado o roto)
    public static boolean isExcelFileValid(MultipartFile file) {
        try (XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(file.getInputStream())) {
            return true; // Si no lanza excepciones, el archivo es válido
        } catch (IOException e) {
            return false; // El archivo está dañado o tiene un formato incorrecto
        }
    }

    // Validar el tipo MIME del archivo utilizando Tika
    public static boolean isValidMimeType(MultipartFile file) {
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getBytes());

            // Verificar que el tipo MIME corresponda a un archivo Excel
            return mimeType.equals("application/vnd.ms-excel") || mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } catch (IOException e) {
            return false; // Error al detectar el tipo MIME
        }
    }

    // Método privado para obtener la extensión del archivo
    private static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex != -1) ? filename.substring(dotIndex + 1) : "";
    }
	
}
