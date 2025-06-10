import axios, { AxiosHeaders } from "axios";

const baseUrl = "https://fuji-bargains-kilometers-indie.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/admin";

class ServicePurchase {
  getPurchasesPaginated(page, size) {
    return axios.get(baseUrl + "/purchases?page=" + page + "&size=" + size);
  }

  getPurchaseById(id) {
    return axios.get(baseUrl +  "/purchases/" + id)
  }

  getPurchaseDetailsById(purchaseId) {
    return axios.get(baseUrl + "/purchase/detailsbyid/" + purchaseId);
  }

  savePurchaseWithDetails(purchase) {
    return axios.post(baseUrl + "/purchase/create", purchase);
  }

  updatePurchaseWithDetails(id, purchase) {
    return axios.put(baseUrl +  "/purchase/update/" + id, purchase);
  }

  deletePurchasesAndDetails(id){
    return axios.delete(baseUrl + "/purchase/delete/" + id);
  }
}

const purchaseServiceInstance = new ServicePurchase();

export default purchaseServiceInstance;
