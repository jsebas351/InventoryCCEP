import React, { useContext, useEffect, useState } from "react";
import ServiceEntry from "../Services/ServiceEntry";
import { WarningButton } from "../Components/GeneralComponents/WarningButton";
import { DangerButton } from "../Components/GeneralComponents/DangerButton";
import { PrimaryButton } from "../Components/GeneralComponents/PrimaryButton";
import { GeneralContext } from "../Context/GeneralContext";
import { Modal } from "../Components/GeneralComponents/Modal";
import { EntryForm } from "../Components/Entries/EntryForm";
import { Pagination } from "../Components/GeneralComponents/Pagination";
import Swal from "sweetalert2";

function Entries() {
  const [entries, setEntries] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [entryToEdit, setEntryToEdit] = useState(null);
  const [loadingCategory, setLoadingEntry] = useState(false);
  const { setOpenModal, openModal, ok, swalCard } = useContext(GeneralContext);

  //Llamado al service para listar entradas
  const EntriesList = (page, size) => {
    ServiceEntry.getEntriesPagination(page, size)
      .then((response) => {
        setEntries(response.data.data.content);
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

  //Listar entradas cada vez que haya un cambio en las paginas y el tamaño
  useEffect(() => {
    EntriesList(page, size);
  }, [page, size]);

  //Funcion para indicarle al formulario que se quiere crear una entrada
  const createEntry = () => {
    // Al abrir el formulario para crear una nueva entradas
    setOpenModal(true);
    setEntryToEdit(null);
  };

  //Funcion para indicarle al formulario que se quiere editar una entrada
  const editEntry = async (id) => {
    try {
      setLoadingEntry(true);
      const entryToEdit = entries.find((entry) => entry.id === id);
      if (entryToEdit) {
        setOpenModal(true);
        setEntryToEdit(entryToEdit);
      }
    } catch (error) {
      console.error("Error fetching entry:", error);
    } finally {
      setLoadingEntry(false);
    }
  };

  //Logica para eliminar una Entrada
  const deleteEntry = (id, name) => {
    Swal.fire({
      title: "¿Está seguro de eliminar " + name + "?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check"></i> Sí, eliminar',
      cancelButtonText: '<i class="fa-solid fa-ban"></i> Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        ServiceEntry.delete(id)
          .then((response) => {
            const { success, message } = response.data;

            if (success) {
              ok(message, "success");
              EntriesList(page, size);
            } else {
              if (response.data.code == 404) {
                swalCard("No se encontro la entrada", message, "error");
              } else {
                swalCard("Error al eliminar la entrada", message, "error");
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
        <h2 className="text-center">Entradas</h2>
        <br />
        <div className="text-center">
          <PrimaryButton execute={createEntry} icon="fa-solid fa-plus" />
        </div>
        <br />
        <div className="table-responsive">
          <table className="table caption-top">
            <caption>Lista de entradas</caption>
            <thead>
              <tr>
                <th scope="col">#</th>
                <th scope="col">Fecha de Entrada</th>
                <th scope="col">Fecha de Edicion</th>
                <th scope="col">Producto</th>
                <th scope="col">Cantidad Ingresada</th>
                <th scope="col">Editar</th>
                <th scope="col">Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {entries.map((entry, i) => (
                <tr key={entry.id}>
                  <td>{page * size + i + 1}</td>
                  <td>{entry.dateEntry}</td>
                  <td>{entry.edit_date || "No ha sido Editada"}</td>
                  <td>{entry.product_id.name}</td>
                  <td>{entry.quantity}</td>
                  <td>
                    <WarningButton
                      icon="fa-solid fa-pen-to-square"
                      execute={() => editEntry(entry.id)}
                    />
                  </td>
                  <td>
                    <DangerButton
                      execute={() => deleteEntry(entry.id, "la Entrada")}
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

      {/* Formulario de entradas */}
      {openModal && (
        <Modal tittle={entryToEdit ? "Editar Entrada" : "Crear Entrada"}>
          <EntryForm
            /* Indicarle al formulario si es para editar o crear */
            EntriesList={() => EntriesList(page, size)}
            editEntry={entryToEdit}
          />
        </Modal>
      )}
    </div>
  );
}

export { Entries };
