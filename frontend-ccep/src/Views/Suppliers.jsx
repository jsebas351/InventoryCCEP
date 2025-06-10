import React, { useContext, useEffect, useState } from "react";
import { PrimaryButton } from "../Components/GeneralComponents/PrimaryButton";
import { WarningButton } from "../Components/GeneralComponents/WarningButton";
import { DangerButton } from "../Components/GeneralComponents/DangerButton";
import { GeneralContext } from "../Context/GeneralContext";
import { SupplierForm } from "../Components/Suppliers/SupplierForm";
import { Modal } from "../Components/GeneralComponents/Modal";
import ServiceSupplier from "../Services/ServiceSupplier";
import { Pagination } from "../Components/GeneralComponents/Pagination";
import Swal from "sweetalert2";

function Suppliers() {
  const [suppliers, setSuppliers] = useState([]);
  const [supplierToEdit, setSupplierToEdit] = useState(null);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [loadingSupplier, setLoadingSupplier] = useState(false);
  const { setOpenModal, openModal, ok, swalCard } = useContext(GeneralContext);

  //LLamado al service para listar los proveedores
  const suppliersList = (page, size) => {
    ServiceSupplier.getAllSuppliersPaginated(page, size)
      .then((response) => {
        setSuppliers(response.data.data.content);
        setTotalPages(response.data.data.totalPages);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  //Actualizar el estado de la pagina = Page
  const handlePageChange = (pageNumber) => {
    setPage(pageNumber - 1);
  };

  //Listar categorias una vez renderizada la pagina
  useEffect(() => {
    suppliersList(page, size);
  }, [page, size]);

  //Funcion para indicarle al formulario si se quiere crear un proveedor
  const createSupplier = () => {
    // Al abrir el formulario para crear una nueva categoría
    setOpenModal(true);
    setSupplierToEdit(null); // Resetea el estado de supplierToEdit seleccionado para evitar edición
  };

  //Funcion para indicarle al formulario que se quiere editar un proveedor
  const editSupplier = async (id) => {
    try {
      setLoadingSupplier(true);
      const response = await ServiceSupplier.byId(id);
      const supplierToEdit = response.data.data;
      if (supplierToEdit) {
        setOpenModal(true);
        setSupplierToEdit(supplierToEdit);
      }
    } catch (error) {
      console.log("Error Cargando los datos: ", error);
    } finally {
      setLoadingSupplier(false);
    }
  };

  //Logica para eliminar un Proveedor
  const deleteSupplier = (id, name) => {
    Swal.fire({
      title: "¿Está seguro de eliminar " + name + "?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check"></i> Sí, eliminar',
      cancelButtonText: '<i class="fa-solid fa-ban"></i> Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        ServiceSupplier.delete(id)
          .then((response) => {
            const { success, message } = response.data;

            if (success) {
              ok(message, "success");
              suppliersList(page, size);
            } else {
              if (response.data.code == 400) {
                swalCard("Producto Relacionado", message, "error");
              } else {
                swalCard("Error al eliminar el proveedor", message, "error");
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
        <h2 className="text-center">Proveedores</h2>
        <br />
        <div className="text-center">
          <PrimaryButton execute={createSupplier} icon="fa-solid fa-plus" />
        </div>
        <br />
        <div className="table-responsive">
          <table className="table caption-top">
            <caption>Lista de Proveedores</caption>
            <thead>
              <tr>
                <th scope="col">#</th>
                <th scope="col">Nit</th>
                <th scope="col">Nombre</th>
                <th scope="col">Telefono</th>
                <th scope="col">Mail</th>
                <th scope="col">Estado</th>
                <th scope="col">Editar</th>
                <th scope="col">Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {suppliers.map((supplier, i) => (
                <tr key={supplier.id}>
                  <td>{i + 1}</td>
                  <td>{supplier.nit}</td>
                  <td>{supplier.name}</td>
                  <td>{supplier.phone}</td>
                  <td>{supplier.mail}</td>
                  <td>{supplier.state}</td>
                  <td>
                    <WarningButton
                      icon="fa-solid fa-pen-to-square"
                      execute={() => editSupplier(supplier.id)}
                    />
                  </td>
                  <td>
                    <DangerButton
                      execute={() => deleteSupplier(supplier.id, supplier.name)}
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
      {/* Formulario de Categorias */}
      {openModal && (
        <Modal tittle={supplierToEdit ? "Editar Proveedor" : "Crear Proveedor"}>
          <SupplierForm
            /* Indicarle al formulario si es para editar o crear */
            suppliersList={() => suppliersList(page, size)}
            editSupplier={supplierToEdit}
          />
        </Modal>
      )}
    </div>
  );
}

export { Suppliers };
