import axios from "axios";

const baseUrl = "https://fuji-bargains-kilometers-indie.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/admin";

class ServiceSupplier {
  getAllSuppliersPaginated(page, size) {
    return axios.get(baseUrl + "/suppliers?page=" + page + "&size=" + size);
  }

  getAllSuppliersNotPaginated() {
    return axios.get(baseUrl + "/suppliersnotpaginated");
  }

  add(supplier) {
    return axios.post(baseUrl + "/suppliers/create", supplier);
  }

  byId(id) {
    return axios.get(baseUrl + "/suppliers/" + id);
  }

  update(id, supplier) {
    return axios.put(baseUrl + "/suppliers/update/" + id, supplier)
  }

  delete(id) {
    return axios.delete(baseUrl + "/suppliers/delete/" + id)
  }

  saveSuppliersExcel(excel) {
    const formData = new FormData();
    formData.append('excel', excel);
  
    return axios.post(baseUrl + "/file/saveSuppliers", formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }
}

const supplierServiceInstance = new ServiceSupplier();

export default supplierServiceInstance;