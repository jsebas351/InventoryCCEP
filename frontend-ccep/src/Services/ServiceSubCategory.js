import axios from "axios";

const baseUrl = "https://fuji-bargains-kilometers-indie.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/admin";

class ServiceSubCategory {
  getAll(page, size) {
    return axios.get(baseUrl + "/subcategories" + `?page=${page}&size=${size}`);
  }
  getAllNotPaginated() {
    return axios.get(baseUrl + "/subcategoriesnotpaginated");
  }

  add(subCategory) {
    return axios.post(baseUrl + "/subcategories/create", subCategory);
  }

  update(id, subCategory) {
    return axios.put(baseUrl + "/subcategories/update/" + id, subCategory)
  }

  delete(id) {
    return axios.delete(baseUrl + "/subcategories/delete/" + id)
  }

  saveSubCategoriesExcel(excel) {
    const formData = new FormData();
    formData.append('excel', excel);
  
    return axios.post(baseUrl + "/file/saveSubCategories", formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }
}

const subCategoryServiceInstance = new ServiceSubCategory();

export default subCategoryServiceInstance;