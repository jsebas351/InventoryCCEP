package com.api.backendCCEP.Facade;

import org.springframework.web.multipart.MultipartFile;

public interface IFilesUpload {

	public void saveFile(MultipartFile file) throws Exception;
	public void saveCategoriesExcel(MultipartFile file) throws Exception;
	public void saveSubCategoriesExcel(MultipartFile file) throws Exception;
	public void saveSuppliersExcel(MultipartFile file) throws Exception;
	public void saveProductsExcel(MultipartFile file) throws Exception;
	public void saveEntriesExcel(MultipartFile file) throws Exception;
	
}
