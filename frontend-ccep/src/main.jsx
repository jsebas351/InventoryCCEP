import React, { StrictMode } from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import "./index.css";

//Bootsrap
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.js";

//FontAwesome
import "@fortawesome/fontawesome-free/css/all.css";

//BoxIcons
import "boxicons/css/boxicons.min.css";

//Router
import { RouteProvider } from "./Router/useRouter.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
  <StrictMode>
    <RouteProvider>
      <App />
    </RouteProvider>
  </StrictMode>
);
