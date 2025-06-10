import React, { useContext, useState } from "react";
import ServicePurchase from "../Services/ServicePurchase";
import { useEffect } from "react";
import { Pagination } from "../Components/GeneralComponents/Pagination";
import { InfoButton } from "../Components/GeneralComponents/InfoButton";
import { WarningButton } from "../Components/GeneralComponents/WarningButton";
import { DangerButton } from "../Components/GeneralComponents/DangerButton";
import { GeneralContext } from "../Context/GeneralContext";
import { Modal } from "../Components/GeneralComponents/Modal";
import Swal from "sweetalert2";
import { useNavigate } from "react-router-dom";

function Purchases() {
  const navigate = useNavigate();
  const [purchases, setPurchases] = useState([]);
  const [detailsPurchases, setDetailsPurchases] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const { setOpenModal, openModal, ok, swalCard } = useContext(GeneralContext);

  // Llamado al service para listar las compras
  const purchasesList = (page, size) => {
    ServicePurchase.getPurchasesPaginated(page, size)
      .then((response) => {
        setPurchases(response.data.data.content);
        setTotalPages(response.data.data.totalPages);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  // Listar los detalles de la compra
  const detailsById = async (purchaseId) => {
    try {
      const response = await ServicePurchase.getPurchaseDetailsById(purchaseId);
      const details = response.data.data;

      if (details) {
        setOpenModal(true);
        setDetailsPurchases(details);
      }
    } catch (error) {
      console.log("Hubo un error listando los detalles: " + error);
    }
  };

  // Listar las compras una vez haya cambios en el estado de page y size
  useEffect(() => {
    purchasesList(page, size);
  }, [page, size]);

  // Actualizar el estado de la pagina = Page
  const handlePageChange = (pageNumber) => {
    setPage(pageNumber - 1);
  };

  // Logica para eliminar las compras con los detalles
  const deletePurchases = (id) => {
    Swal.fire({
      title: "¿Está seguro de eliminar la Compra?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check"></i> Sí, eliminar',
      cancelButtonText: '<i class="fa-solid fa-ban"></i> Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        ServicePurchase.deletePurchasesAndDetails(id)
          .then((response) => {
            const { success, message } = response.data;

            if (success) {
              ok(message, "success");
              purchasesList(page, size);
            } else {
              if (response.data.code == 404) {
                swalCard("No se encontro la venta", message, "error");
              } else {
                swalCard("Error al eliminar la Venta", message, "error");
              }
            }
          })
          .catch((error) => {
            console.log(error.data.message);
            console.log(error);
          });
      }
    });
  };

  return (
    <div>
      <div className="container">
        <br />
        <h2 className="text-center">Compras</h2>
        <div className="table-responsive">
          <table className="table caption-top">
            <caption>Lista de Compras</caption>
            <thead>
              <tr>
                <th scope="col">#</th>
                <th scope="col">Total</th>
                <th scope="col">Numero de Factura</th>
                <th scope="col">Fecha de la Compra</th>
                <th scope="col">Fecha de Edición</th>
                <th scope="col">Proveedor</th>
                <th scope="col">Estado</th>
                <th scope="col">Detalles</th>
                <th scope="col">Editar</th>
                <th scope="col">Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {purchases.map((purchase, i) => (
                <tr key={purchase.id}>
                  <td>{page * size + i + 1}</td>
                  <td>${purchase.total_purchase.toLocaleString("es-CO")}</td>
                  <td>{purchase.bill_number}</td>
                  <td>{purchase.purchase_date}</td>
                  <td>{purchase.edit_date || "No ha sido editada"}</td>
                  <td>{purchase.provider_id.name}</td>
                  <td>{purchase.state}</td>
                  <td>
                    <InfoButton
                      icon={"fa-solid fa-circle-info"}
                      execute={() => detailsById(purchase.id)}
                    />
                  </td>
                  <td>
                    <WarningButton
                      icon="fa-solid fa-pen-to-square"
                      execute={() => navigate(`/dashboard/purchase-update/${purchase.id}`)}
                    />
                  </td>
                  <td>
                    <DangerButton
                      execute={() => deletePurchases(purchase.id,)}
                      icon="fa-solid fa-trash-can"
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <Pagination
            totalPages={totalPages}
            currentPage={page + 1}
            onPageChange={handlePageChange}
          />
        </div>
      </div>

      {openModal && (
        <Modal tittle={"Detalles de la compra"}>
          <div className="table-responsive">
            <table className="table caption-top">
              <caption>Detalles de la Compra</caption>
              <thead>
                <tr>
                  <th scope="col">#</th>
                  <th scope="col">Cantidad</th>
                  <th scope="col">Subtotal</th>
                  <th scope="col">Producto</th>
                </tr>
              </thead>
              <tbody>
                {detailsPurchases.map((detail, i) => (
                  <tr key={detail.id}>
                    <td>{page * size + i + 1}</td>
                    <td>{detail.quantity}</td>
                    <td>${detail.subtotal.toLocaleString("es-CO")}</td>
                    <td>{detail.product_id.name}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Modal>
      )}
    </div>
  );
}

export { Purchases };
