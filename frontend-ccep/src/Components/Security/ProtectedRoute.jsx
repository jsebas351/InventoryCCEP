import React, { useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import Cookies from "js-cookie";
import Swal from "sweetalert2";
import { jwtDecode } from "jwt-decode";
import ServiceAuth from "../../Services/ServiceAuth";

function ProtectedRoute({ element, requiresAuth, allowedRoles }) {
  const [authStatus, setAuthStatus] = useState({
    isAuthenticated: null,
    hasPermission: null,
  });
  const [loading, setLoading] = useState(true);
  const token = Cookies.get("authToken");
  const location = useLocation();

  useEffect(() => {
    const checkAuth = async () => {
      if (!requiresAuth) {
        setAuthStatus({ isAuthenticated: true, hasPermission: true });
        setLoading(false);
        return;
      }

      if (!token) {
        setAuthStatus({ isAuthenticated: false, hasPermission: false });
        setLoading(false);
        return;
      }

      try {
        const decodedToken = jwtDecode(token);
        const currentTime = Math.floor(Date.now() / 1000);
        const timeLeft = decodedToken.exp - currentTime;

        if (timeLeft <= 0) throw new Error("Token expired");

        const response = await ServiceAuth.verifyToken(token);
        if (!response.data.success) throw new Error("Invalid token");

        const user = JSON.parse(Cookies.get("user"));
        const userRole = user.roles[0].name_role;
        const hasPermission = allowedRoles.includes(userRole);

        setAuthStatus({ isAuthenticated: true, hasPermission });

        if (timeLeft <= 300) handleTokenExpiryWarning();
      } catch {
        setAuthStatus({ isAuthenticated: false, hasPermission: false });
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, [token, requiresAuth, allowedRoles]);

  const handleTokenExpiryWarning = () => {
    Swal.fire({
      title: "Sesión a punto de expirar",
      text: "Su sesión está a punto de expirar. ¿Desea cerrar sesión y volver a autenticarse?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí, cerrar sesión",
      cancelButtonText: "Cancelar",
    }).then((result) => {
      if (result.isConfirmed) {
        Cookies.remove("authToken");
        setAuthStatus({ isAuthenticated: false, hasPermission: false });
      }
    });
  };

  if (loading)
    return (
      <div>
        <i className="fa-solid fa-arrow-rotate-right fa-spin"></i>
      </div>
    );

  if (!authStatus.isAuthenticated) {
    return <Navigate to="/" state={{ from: location }} replace />;
  }

  if (!authStatus.hasPermission) {
    return <h1>No tiene permisos</h1>;
  }

  return element;
}

export { ProtectedRoute };
