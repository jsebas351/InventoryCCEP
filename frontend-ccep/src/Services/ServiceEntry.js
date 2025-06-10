import axios from "axios";

const baseUrl = "https://gmt-jan-profit-worlds.trycloudflare.com/backendCCEP-0.0.1-SNAPSHOT/admin";

class ServiceEntry {
  getEntriesPagination(page, size) {
    return axios.get(baseUrl + "/entries" + `?page=${page}&size=${size}`);
  }

  add(entry) {
    return axios.post(baseUrl + "/entries/create", entry);
  }

  update(id, entry) {
    return axios.put(baseUrl + "/entries/update/" + id, entry)
  }

  delete(id) {
    return axios.delete(baseUrl + "/entries/delete/" + id)
  }

  saveEntriesExcel(excel) {
    const formData = new FormData();
    formData.append('excel', excel);
  
    return axios.post(baseUrl + "/file/saveEntries", formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }
}

const entryServiceInstance = new ServiceEntry();

export default entryServiceInstance;