import axios from "axios";

const baseUrl = "https://fuji-bargains-kilometers-indie.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/admin";

class ServiceReport {
  getSalesSummary(startDate, endDate) {
    return axios.get(baseUrl + "/report/summary" + `?startDate=${startDate}&endDate=${endDate}`);
  }
}

const reportServiceInstance = new ServiceReport();

export default reportServiceInstance;
