import axios from "axios";

const baseUrl = "https://fuji-bargains-kilometers-indie.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/admin";

class ServiceInventory {

    getInventory(page, size){
        return axios.get(baseUrl + "/inventories" + `?page=${page}&size=${size}`);
    }

}

const inventoryServiceInstance = new ServiceInventory();

export default inventoryServiceInstance;