import React, { useContext, useEffect, useState } from "react";
import { GeneralContext } from "../Context/GeneralContext";
import { PrimaryButton } from "../Components/GeneralComponents/PrimaryButton";
import { WarningButton } from "../Components/GeneralComponents/WarningButton";
import { Pagination } from "../Components/GeneralComponents/Pagination";
import { Modal } from "../Components/GeneralComponents/Modal";
import { DangerButton } from "../Components/GeneralComponents/DangerButton";
import { ProductForm } from "../Components/Products/ProductForm";
import ServiceProduct from "../Services/ServiceProduct";
import Swal from "sweetalert2";

function Products() {
  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [productToEdit, setProductToEdit] = useState(false);
  const [loadingProduct, setLoadingProduct] = useState(false);
  const { setOpenModal, openModal, ok, swalCard } = useContext(GeneralContext);

  //Llamado al service para listar los productos
  const productsList = (page, size) => {
    ServiceProduct.getAllProductsPaginated(page, size)
      .then((response) => {
        setProducts(response.data.data.content);
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

  //Listar productos una vez haya cambios en los estados page y size
  useEffect(() => {
    productsList(page, size);
  }, [page, size]);

  //Funcion para indicarle al formulario que se quiere crear un producto
  const createProduct = () => {
    // Al abrir el formulario para crear un nuevo producto
    setOpenModal(true);
    setProductToEdit(null); // Resetea el producto seleccionado para evitar edición
  };

  //Funcion para indicarle al formulario que se quiere editar un producto
  const editProduct = (id) => {
    try {
      setLoadingProduct(true);
      const productToEdit = products.find((Product) => Product.id === id);
      if (productToEdit) {
        setOpenModal(true);
        setProductToEdit(productToEdit);
      }
    } catch (error) {
      console.error("Error fetching Product:", error);
    } finally {
      setLoadingProduct(false);
    }
  };

  //Logica para eliminar un producto
  const deleteProduct = (id, name) => {
    Swal.fire({
      title: "¿Está seguro de eliminar " + name + "?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check"></i> Sí, eliminar',
      cancelButtonText: '<i class="fa-solid fa-ban"></i> Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        ServiceProduct.delete(id)
          .then((response) => {
            const { success, message } = response.data;

            if (success) {
              ok(message, "success");
              productsList(page, size);
            } else {
              if (response.data.code === 400) {
                swalCard("Producto Vendido", message, "error");
              } else {
                swalCard("Error al eliminar el Producto", message, "error");
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
        <h2 className="text-center">Productos</h2>
        <br />
        <div className="text-center">
          <PrimaryButton execute={createProduct} icon="fa-solid fa-plus" />
        </div>
        <br />
        <div className="table-responsive">
          <table className="table caption-top">
            <caption>Lista de Productos</caption>
            <thead>
              <tr>
                <th scope="col">#</th>
                <th scope="col">Referencia</th>
                <th scope="col">Nombre</th>
                <th scope="col">Descripcion</th>
                <th scope="col">Precio de Compra</th>
                <th scope="col">Precio de Venta</th>
                <th scope="col">SubCategoria</th>
                <th scope="col">Proveedor</th>
                <th scope="col">Estado</th>
                <th scope="col">Editar</th>
                <th scope="col">Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product, i) => (
                <tr key={product.id}>
                  <td>{page * size + i + 1}</td>
                  <td>{product.reference}</td>
                  <td>{product.name}</td>
                  <td>{product.description}</td>
                  <td>${product.purchase_price.toLocaleString("es-CO")}</td>
                  <td>${product.sale_price.toLocaleString("es-CO")}</td>
                  <td>{product.subcategory_id.name}</td>
                  <td>{product.provider_id.name}</td>
                  <td>{product.state}</td>
                  <td>
                    <WarningButton
                      icon="fa-solid fa-pen-to-square"
                      execute={() => editProduct(product.id)}
                    />
                  </td>
                  <td>
                    <DangerButton
                      execute={() => deleteProduct(product.id, product.name)}
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

      {/* Formulario de Productos */}
      {openModal && (
        <Modal tittle={productToEdit ? "Editar Producto" : "Crear Producto"}>
          <ProductForm
            /* Indicarle al formulario si es para editar o crear */
            productsList={() => productsList(page, size)}
            editProduct={productToEdit}
          />
        </Modal>
      )}
    </div>
  );
}

export { Products };
