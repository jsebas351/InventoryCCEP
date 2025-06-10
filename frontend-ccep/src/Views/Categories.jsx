import React, { useContext, useEffect, useState } from "react";
import ServiceCategory from "../Services/ServiceCategory";
import { WarningButton } from "../Components/GeneralComponents/WarningButton";
import { DangerButton } from "../Components/GeneralComponents/DangerButton";
import { PrimaryButton } from "../Components/GeneralComponents/PrimaryButton";
import { GeneralContext } from "../Context/GeneralContext";
import { Modal } from "../Components/GeneralComponents/Modal";
import { CategoryForm } from "../Components/Categories/CategoryForm";
import { Pagination } from "../Components/GeneralComponents/Pagination";
import Swal from "sweetalert2";

function Categories() {
  const [categories, setCategories] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [categoryToEdit, setCategoryToEdit] = useState(null);
  const [loadingCategory, setLoadingCategory] = useState(false);
  const { setOpenModal, openModal, ok, swalCard } = useContext(GeneralContext);

  //Llamado al service para listar Categorias
  const categoriesList = (page, size) => {
    ServiceCategory.getCategoriesPagination(page, size)
      .then((response) => {
        setCategories(response.data.data.content);
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

  //Listar categorias cada vez que haya un cambio en las paginas y el tamaño
  useEffect(() => {
    categoriesList(page, size);
  }, [page, size]);

  //Funcion para indicarle al formulario que se quiere crear una categoria
  const createCategory = () => {
    // Al abrir el formulario para crear una nueva categoría
    setOpenModal(true);
    setCategoryToEdit(null);
  };

  //Funcion para indicarle al formulario que se quiere editar una categoria
  const editCategory = async (id) => {
    try {
      setLoadingCategory(true);
      const response = await ServiceCategory.byId(id);
      const categoryToEdit = response.data.data;
      if (categoryToEdit) {
        setOpenModal(true);
        setCategoryToEdit(categoryToEdit);
      }
    } catch (error) {
      console.error("Error fetching category:", error);
    } finally {
      setLoadingCategory(false);
    }
  };

  //Logica para eliminar una Categoria
  const deleteCategory = (id, name) => {
    Swal.fire({
      title: "¿Está seguro de eliminar " + name + "?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check"></i> Sí, eliminar',
      cancelButtonText: '<i class="fa-solid fa-ban"></i> Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        ServiceCategory.delete(id)
          .then((response) => {
            const { success, message } = response.data;

            if (success) {
              ok(message, "success");
              categoriesList(page, size);
            } else {
              if (response.data.code == 400) {
                swalCard("SubCategoria Relacionada", message, "error");
              } else {
                swalCard("Error al eliminar la categoría", message, "error");
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
        <h2 className="text-center">Categorias</h2>
        <br />
        <div className="text-center">
          <PrimaryButton execute={createCategory} icon="fa-solid fa-plus" />
        </div>
        <br />
        <div className="table-responsive">
          <table className="table caption-top">
            <caption>Lista de Categorias</caption>
            <thead>
              <tr>
                <th scope="col">#</th>
                <th scope="col">Nombre</th>
                <th scope="col">Estado</th>
                <th scope="col">Editar</th>
                <th scope="col">Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((category, i) => (
                <tr key={category.id}>
                  <td>{page * size + i + 1}</td>
                  <td>{category.name}</td>
                  <td>{category.state}</td>
                  <td>
                    <WarningButton
                      icon="fa-solid fa-pen-to-square"
                      execute={() => editCategory(category.id)}
                    />
                  </td>
                  <td>
                    <DangerButton
                      execute={() => deleteCategory(category.id, category.name)}
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
        <Modal tittle={categoryToEdit ? "Editar Categoría" : "Crear Categoría"}>
          <CategoryForm
            /* Indicarle al formulario si es para editar o crear */
            categoriesList={() => categoriesList(page, size)}
            editCategory={categoryToEdit}
          />
        </Modal>
      )}
    </div>
  );
}

export { Categories };
