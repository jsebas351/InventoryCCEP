import React, { useContext } from "react";
import { RouterProvider } from "react-router-dom";
import { RouteContext } from "./Router/useRouter";
import axios from "axios";
import Cookies from "js-cookie";

function App() {
  axios.interceptors.request.use(
    (config) => {
      const token = Cookies.get("authToken");

      if (token) {
        config.headers["Authorization"] = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      console.log("Error en la solicitud");
      return Promise.reject(error);
    }
  );

  const { router } = useContext(RouteContext);

  return (
    <>
      <RouterProvider router={router}></RouterProvider>
    </>
  );
}

export default App;
