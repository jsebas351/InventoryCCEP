import React from "react";
import "../../Styles/NavBar/NavBar.css";
import { NavLink, useNavigate } from "react-router-dom";
import Cookies from "js-cookie";

function NavBar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    Cookies.remove("authToken");
    Cookies.remove("user");

    navigate("/");
  };

  const user = JSON.parse(Cookies.get("user"));
  const userRole = user.roles[0].name_role;

  return (
    <nav className="navbar navbar-expand-lg fixed-top">
      <div className="container-fluid">
        <a className="navbar-brand" href="#">
          <i className="fa-solid fa-paw icon"></i> CCEP
        </a>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div
          className="collapse navbar-collapse justify-content-end"
          id="navbarNav"
        >
          <ul className="navbar-nav">
            <li className="nav-item text-ccep">
              <a className="nav-link active" aria-current="page" href="#">
                Inicio
              </a>
            </li>
            <li className="nav-item">
              <div className="nav-link">
                <NavLink to="pos">Realizar Venta</NavLink>
              </div>
            </li>
            {userRole === "Administrador" ? (
              <li className="nav-item">
                <div className="nav-link">
                  <NavLink to="edc">Realizar Compra</NavLink>
                </div>
              </li>
            ) : null}
            <li className="nav-item">
              <div
                className="nav-link"
                onClick={handleLogout}
                style={{ cursor: "pointer" }}
              >
                Cerrar Sesión
              </div>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}

export { NavBar };
