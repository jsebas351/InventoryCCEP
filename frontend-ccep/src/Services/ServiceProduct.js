import axios from "axios";

const baseUrl = "https://fuji-bargains-kilometers-indie.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/admin";

class ServiceProduct {
  getAllProductsPaginated(page, size) {
    return axios.get(baseUrl + "/products?page=" + page + "&size=" + size);
  }

  getAllProductsNotPaginated() {
    return axios.get(baseUrl + "/productnotpaginated");
  }

  add(product) {
    return axios.post(baseUrl + "/products/create", product);
  }

  byId(id) {
    return axios.get(baseUrl + "/products/" + id);
  }

  update(id, product) {
    return axios.put(baseUrl + "/products/update/" + id, product)
  }

  delete(id) {
    return axios.delete(baseUrl + "/products/delete/" + id)
  }

  filteredProducts(value){
    return axios.post(baseUrl + "/products/search", value)
  }

  findProductByProvider(providerId){
    return axios.get(baseUrl + "/products/filter-product-provider/" + providerId);
  }

  saveProductsExcel(excel) {
    const formData = new FormData();
    formData.append('excel', excel);
  
    return axios.post(baseUrl + "/file/saveProducts", formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

}

const productServiceInstance = new ServiceProduct();

export default productServiceInstance;