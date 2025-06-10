import axios from "axios";

const baseUrl = "https://gmt-jan-profit-worlds.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/auth";

class ServiceAuth {

    authentication(login) {
        return axios.post(baseUrl + "/login", login)
    }

    verifyToken(token) {
        return axios.post(baseUrl + "/validate-token", token)
    }

}

const authServiceInstance = new ServiceAuth();

export default authServiceInstance;