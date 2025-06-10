import axios from "axios";

const baseUrl = "https://fuji-bargains-kilometers-indie.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/vendor";

class ServiceSale {

  getSalesList(page, size) {
    return axios.get(baseUrl + "/sales?page=" + page + "&size=" + size);
  }

  getSaleById(id) {
    return axios.get(baseUrl + "/sales/" + id);
  }

  getSaleDetailById(saleId) {
    return axios.get(baseUrl + "/sales/detailsbyid/" + saleId);
  }

  getAllPaymentsMethods() {
    return axios.get(baseUrl + "/paymentmethod");
  }

  saveSaleWithDetails(saleData) {
    return axios.post(baseUrl + "/sales/create", saleData);
  }

  updateSaleWithDetails(id, saleData){
    return axios.put(baseUrl + "/sales/update/" + id, saleData);
  }

  deleteSaleWithDetails(saleId){
    return axios.delete(baseUrl + "/sales/delete/" + saleId)
  }

}

const saleServiceInstance = new ServiceSale();

export default saleServiceInstance;