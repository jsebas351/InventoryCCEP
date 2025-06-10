import React, { useContext, useEffect, useState } from "react";
import ServiceSubCategory from "../Services/ServiceSubCategory";
import { WarningButton } from "../Components/GeneralComponents/WarningButton";
import { DangerButton } from "../Components/GeneralComponents/DangerButton";
import { PrimaryButton } from "../Components/GeneralComponents/PrimaryButton";
import { GeneralContext } from "../Context/GeneralContext";
import { Modal } from "../Components/GeneralComponents/Modal";
import { SubCategoryForm } from "../Components/SubCategories/SubCategoriesForm";
import { Pagination } from "../Components/GeneralComponents/Pagination";
import Swal from "sweetalert2";

function SubCategories() {
  const [subCategories, setSubCategories] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [subCategoryToEdit, setSubCategoryToEdit] = useState(null);
  const [loadingCategory, setLoadingSubCategory] = useState(false);
  const { setOpenModal, openModal, ok, swalCard } = useContext(GeneralContext);

  //Llamado al service para listar SubCategorias
  const subCategoriesList = (page, size) => {
    ServiceSubCategory.getAll(page, size)
      .then((response) => {
        setSubCategories(response.data.data.content);
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

  //Listar categorias una vez haya cambios en los estados de page y size
  useEffect(() => {
    subCategoriesList(page, size);
  }, [page, size]);

  //Funcion para indicarle al formulario que se quiere crear una subcategoria
  const createSubCategory = () => {
    // Al abrir el formulario para crear una nueva categoría
    setOpenModal(true);
    setSubCategoryToEdit(null); // Resetea la categoría seleccionada para evitar edición
  };

  //Funcion para indicarle al formulario que se quiere editar una subcategoria
  const editSubCategory = (id) => {
    try {
      setLoadingSubCategory(true);
      const subCategoryToEdit = subCategories.find(
        (SubCategory) => SubCategory.id === id
      );
      if (subCategoryToEdit) {
        setOpenModal(true);
        setSubCategoryToEdit(subCategoryToEdit);
      }
    } catch (error) {
      console.error("Error fetching SubCategory:", error);
    } finally {
      setLoadingSubCategory(false);
    }
  };

  //Logica para eliminar una Categoria
  const deleteSubCategory = (id, name) => {
    Swal.fire({
      title: "¿Está seguro de eliminar " + name + "?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check"></i> Sí, eliminar',
      cancelButtonText: '<i class="fa-solid fa-ban"></i> Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        ServiceSubCategory.delete(id)
          .then((response) => {
            const { success, message } = response.data;

            if (success) {
              ok(message, "success");
              subCategoriesList(page, size);
            } else {
              if (response.data.code == 400) {
                swalCard("Producto Relacionado", message, "error");
              } else {
                swalCard("Error al eliminar la subcategoría", message, "error");
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
        <h2 className="text-center">SubCategorias</h2>
        <br />
        <div className="text-center">
          <PrimaryButton execute={createSubCategory} icon="fa-solid fa-plus" />
        </div>
        <br />
        <div className="table-responsive">
          <table className="table caption-top">
            <caption>Lista de SubCategorias</caption>
            <thead>
              <tr>
                <th scope="col">#</th>
                <th scope="col">Nombre</th>
                <th scope="col">Categoria</th>
                <th scope="col">Estado</th>
                <th scope="col">Editar</th>
                <th scope="col">Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {subCategories.map((SubCategory, i) => (
                <tr key={SubCategory.id}>
                  <td>{page * size + i + 1}</td>
                  <td>{SubCategory.name}</td>
                  <td>{SubCategory.category_id.name}</td>
                  <td>{SubCategory.state}</td>
                  <td>
                    <WarningButton
                      icon="fa-solid fa-pen-to-square"
                      execute={() => editSubCategory(SubCategory.id)}
                    />
                  </td>
                  <td>
                    <DangerButton
                      execute={() =>
                        deleteSubCategory(SubCategory.id, SubCategory.name)
                      }
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
        <Modal
          tittle={
            subCategoryToEdit ? "Editar SubCategoría" : "Crear SubCategoría"
          }
        >
          <SubCategoryForm
            /* Indicarle al formulario si es para editar o crear */
            subCategoriesList={() => subCategoriesList(page, size)}
            editSubCategory={subCategoryToEdit}
          />
        </Modal>
      )}
    </div>
  );
}

export { SubCategories };
