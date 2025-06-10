import React, { useState } from "react";

//React Router
import { createBrowserRouter } from "react-router-dom";

//Views
import { Login } from "../Views/Login";
import { DashBoard } from "../Views/DashBoard";
import { StartPage } from "../Views/StarPage";
import { Categories } from "../Views/Categories";
import { SubCategories } from "../Views/SubCategories";
import { Suppliers } from "../Views/Suppliers";
import { Products } from "../Views/Products";
import { PosSystem } from "../Views/PosSystem";
import { Sales } from "../Views/Sales";
import { UpdateSale } from "../Components/Sales/SalesViews/UpdateSale";
import { EdcSystem } from "../Views/EdcSystem";
import { Purchases } from "../Views/Purchases";
import { UpdatePurchase } from "../Components/Purchases/UpdatePurchase";
import { Inventories } from "../Views/Inventories";
import { ProtectedRoute } from "../Components/Security/ProtectedRoute";
import { Entries } from "../Views/Entries";

const RouteContext = React.createContext();

function RouteProvider(props) {
  const router = createBrowserRouter([
    {
      path: "/",
      element: (
        <ProtectedRoute
          element={<Login />}
          requiresAuth={false}
          allowedRoles={[]}
        />
      ),
    },
    {
      path: "/dashboard",
      element: (
        <ProtectedRoute
          element={<DashBoard />}
          requiresAuth={true}
          allowedRoles={["Administrador", "Vendedor"]}
        />
      ),
      children: [
        {
          path: "index",
          element: (
            <ProtectedRoute
              element={<StartPage />}
              requiresAuth={true}
              allowedRoles={["Administrador", "Vendedor"]}
            />
          ),
        },
        {
          path: "categories",
          element: (
            <ProtectedRoute
              element={<Categories />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "subcategories",
          element: (
            <ProtectedRoute
              element={<SubCategories />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "suppliers",
          element: (
            <ProtectedRoute
              element={<Suppliers />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "products",
          element: (
            <ProtectedRoute
              element={<Products />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "pos",
          element: (
            <ProtectedRoute
              element={<PosSystem />}
              requiresAuth={true}
              allowedRoles={["Administrador", "Vendedor"]}
            />
          ),
        },
        {
          path: "sales",
          element: (
            <ProtectedRoute
              element={<Sales />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "sale-update/:id",
          element: (
            <ProtectedRoute
              element={<UpdateSale />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "edc",
          element: (
            <ProtectedRoute
              element={<EdcSystem />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "purchases",
          element: (
            <ProtectedRoute
              element={<Purchases />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "purchase-update/:id",
          element: (
            <ProtectedRoute
              element={<UpdatePurchase />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "inventories",
          element: (
            <ProtectedRoute
              element={<Inventories />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
        {
          path: "entries",
          element: (
            <ProtectedRoute
              element={<Entries />}
              requiresAuth={true}
              allowedRoles={["Administrador"]}
            />
          ),
        },
      ],
    },
  ]);

  return (
    <RouteContext.Provider value={{ router }}>
      {props.children}
    </RouteContext.Provider>
  );
}

export { RouteContext, RouteProvider };
