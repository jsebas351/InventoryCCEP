import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import ServiceSale from "../Services/ServiceSale";
import { Pagination } from "../Components/GeneralComponents/Pagination";
import { DangerButton } from "../Components/GeneralComponents/DangerButton";
import { WarningButton } from "../Components/GeneralComponents/WarningButton";
import { InfoButton } from "../Components/GeneralComponents/InfoButton";
import { GeneralContext } from "../Context/GeneralContext";
import { Modal } from "../Components/GeneralComponents/Modal";
import Swal from "sweetalert2";

function Sales() {
  const navigate = useNavigate();
  const [sales, setSales] = useState([]);
  const [sales_details, setSales_details] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const { setOpenModal, openModal, ok, swalCard } = useContext(GeneralContext);

  // Llamado al service para listar las ventas
  const saleList = (page, size) => {
    ServiceSale.getSalesList(page, size)
      .then((response) => {
        setSales(response.data.data.content);
        setTotalPages(response.data.data.totalPages);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const detailsById = async (saleId) => {
    try {
      const response = await ServiceSale.getSaleDetailById(saleId);
      const details = response.data.data;

      if (details) {
        setOpenModal(true);
        setSales_details(details);
      }
    } catch (error) {
      console.log("Hubo un error listando los detalles: " + error);
    }
  };

  // Listar las ventas cada vez que haya un cambio en la pagina o en el tamaño
  useEffect(() => {
    saleList(page, size);
  }, [page, size]);

  // Actualizar el estado de la pagina = Page
  const handlePageChange = (pageNumber) => {
    setPage(pageNumber - 1);
  };

  //Logica para eliminar la venta con los detalles
  const deleteSales = (id) => {
    Swal.fire({
      title: "¿Está seguro de eliminar la venta?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check"></i> Sí, eliminar',
      cancelButtonText: '<i class="fa-solid fa-ban"></i> Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        ServiceSale.deleteSaleWithDetails(id)
          .then((response) => {
            const { success, message } = response.data;

            if (success) {
              ok(message, "success");
              saleList(page, size);
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
        <h2 className="text-center">Ventas</h2>
        <br />
        <br />
        <div className="table-responsive">
          <table className="table caption-top">
            <caption>Lista de Ventas</caption>
            <thead>
              <tr>
                <th scope="col">#</th>
                <th scope="col">Total</th>
                <th scope="col">Fecha de la Venta</th>
                <th scope="col">Fecha de Edición</th>
                <th scope="col">Descuento</th>
                <th scope="col">Usuario</th>
                <th scope="col">Metodo de Pago</th>
                <th scope="col">Estado</th>
                <th scope="col">Detalles</th>
                <th scope="col">Editar</th>
                <th scope="col">Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {sales.map((sale, i) => (
                <tr key={sale.id}>
                  <td>{page * size + i + 1}</td>
                  <td>${sale.total_sale.toLocaleString("es-CO")}</td>
                  <td>{sale.sale_date}</td>
                  <td>{sale.edit_date || "No ha sido editada"}</td>
                  <td>${sale.discount.toLocaleString("es-CO")}</td>
                  <td>{sale.user_id}</td>
                  <td>{sale.paymethod_id.name}</td>
                  <td>{sale.state}</td>
                  <td>
                    <InfoButton
                      icon={"fa-solid fa-circle-info"}
                      execute={() => detailsById(sale.id)}
                    />
                  </td>
                  <td>
                    <WarningButton
                      icon={"fa-solid fa-pen-to-square"}
                      execute={() => navigate(`/dashboard/sale-update/${sale.id}`)}
                    />
                  </td>
                  <td>
                    <DangerButton
                      execute={() => deleteSales(sale.id)}
                      icon={"fa-solid fa-trash-can"}
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

      {/* Mostrar los detalles de la venta */}
      {openModal && (
        <Modal tittle={"Detalles de la venta"}>
          <div className="table-responsive">
            <table className="table caption-top">
              <caption>Detalles de la Venta</caption>
              <thead>
                <tr>
                  <th scope="col">#</th>
                  <th scope="col">Cantidad</th>
                  <th scope="col">Subtotal</th>
                  <th scope="col">Producto</th>
                  <th scope="col">Descuento del Producto</th>
                </tr>
              </thead>
              <tbody>
                {sales_details.map((sale_detail, i) => (
                  <tr key={sale_detail.id}>
                    <td>{page * size + i + 1}</td>
                    <td>{sale_detail.quantity}</td>
                    <td>${sale_detail.subtotal.toLocaleString("es-CO")}</td>
                    <td>{sale_detail.product_id.name}</td>
                    <td>
                      ${sale_detail.discount_product.toLocaleString("es-CO")}
                    </td>
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

export { Sales };
