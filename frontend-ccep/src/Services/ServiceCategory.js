import axios from "axios";

const baseUrl = "https://gmt-jan-profit-worlds.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/admin";

class ServiceCategory {
  getCategoriesPagination(page, size) {
    return axios.get(baseUrl + "/categories" + `?page=${page}&size=${size}`);
  }

  getCategoriesNotPaginated() {
    return axios.get(baseUrl + "/categoriesnotpaginated");
  }

  add(category) {
    return axios.post(baseUrl + "/categories/create", category);
  }

  byId(id) {
    return axios.get(baseUrl + "/categories/" + id);
  }

  update(id, category) {
    return axios.put(baseUrl + "/categories/update/" + id, category)
  }

  delete(id) {
    return axios.delete(baseUrl + "/categories/delete/" + id)
  }

  saveCategoriesExcel(excel) {
    const formData = new FormData();
    formData.append('excel', excel);
  
    return axios.post(baseUrl + "/file/saveCategories", formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }
}

const categoryServiceInstance = new ServiceCategory();

export default categoryServiceInstance;