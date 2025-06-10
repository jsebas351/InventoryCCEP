import React, { useContext, useEffect, useState } from "react";
// import "../../Styles/Subproducts/EntryForm.css";
import ServiceEntry from "../../Services/ServiceEntry";
import ServiceProduct from "../../Services/ServiceProduct";
import { GeneralContext } from "../../Context/GeneralContext";

function EntryForm({ EntriesList, editEntry }) {
  //Campos del json y del formulario
  const [quantity, setQuantity] = useState("");
  const [product_id, setProduct_id] = useState("");
  const [isEditMode, setIsEditMode] = useState(false);

  //Estado para saber si se quiere agregar entradas con un excel
  const [excelMode, setExcelMode] = useState(false);
  const [excelEntries, setExcelEntries] = useState(null);

  //Estados para mostrar los errores de validacion
  const [quantityError, setQuantityError] = useState("");
  const [productError, setProductError] = useState("");
  const [fileError, setFileError] = useState("");

  const [products, setProducts] = useState([]);

  //Contexto General
  const { closeModal, ok, swalCard } = useContext(GeneralContext);

  const onStatusChange = (e) => {
    setState(e.target.value);
  };

  const onFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Validar que sea un archivo Excel
      const fileExtension = file.name.split(".").pop().toLowerCase();
      const allowedExtensions = ["xlsx", "xls"];

      if (!allowedExtensions.includes(fileExtension)) {
        setFileError("Por favor, selecciona un archivo Excel (.xlsx o .xls).");
        setExcelEntries(null);
        return;
      }

      // Validar el tamaño del archivo
      const maxFileSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxFileSize) {
        setFileError(
          "El archivo es demasiado grande. El tamaño máximo permitido es 5MB."
        );
        setExcelEntries(null);
        return;
      }

      setFileError("");
      setExcelEntries(file);
    }
  };

  const onExcelModeChange = (e) => {
    setExcelMode(e.target.checked);
    setFileError("");
    setExcelEntries(null);
  };

  const onProductIdChange = (e) => {
    setProduct_id(e.target.value);
  };

  //Llamado al service para listar productos una vez terminada la operacion de editar o crear
  const productsList = () => {
    ServiceProduct.getAllProductsNotPaginated()
      .then((response) => {
        setProducts(response.data.data);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    productsList();
  }, []);

  useEffect(() => {
    // Si se proporciona una Entrada para editar, establecer el modo de edición y llenar los campos.
    if (editEntry) {
      setIsEditMode(true);
      setQuantity(editEntry.quantity);
      setProduct_id(editEntry.product_id.id);
    } else {
      // Si no se proporciona una Entrada para editar, resetear el formulario.
      setQuantity("");
      setProduct_id();
      setIsEditMode(false);
    }
  }, [editEntry]);

  //Metodo para crear o editar subcategorias
  const saveOrUpdate = async (e) => {
    e.preventDefault();

    //Campos del json para enviar al servidor
    const updatedEntry = { quantity, product_id };

    if (!excelMode) {
      //Validacion de campos
      if (quantity.trim() === "") {
        setQuantityError("Este campo es requerido");
        return;
      } else {
        setQuantityError("");
      }

      if (product_id.toString().trim() === "") {
        setProductError("Este campo es requerido");
        return;
      } else {
        setProductError("");
      }
    }

    try {
      //Indicar que se quiere editar
      if (isEditMode) {
        const response = await ServiceEntry.update(editEntry.id, updatedEntry);
        ok(response.data.message, "success");
      } else if (excelMode) {
        if (!excelEntries) {
          setFileError("Debes seleccionar un archivo Excel para continuar.");
          return;
        }

        const response = await ServiceEntry.saveEntriesExcel(excelEntries);
        ok(response.data.message, "success");

        if (
          response.data.code == 400 ||
          response.data.code == 415 ||
          response.data.code == 422
        ) {
          swalCard("Error al subir el Archivo", response.data.message, "error");
        }
      } else {
        //Indicar que se quiere crear
        const response = await ServiceEntry.add(updatedEntry);
        ok(response.data.message, "success");
      }

      closeModal();
      EntriesList();
    } catch (error) {
      console.error(error.message);
      swalCard("Hubo un problema", error.message, "error");
      closeModal();
    }
  };

  return (
    <form>
      {!isEditMode && (
        <div className="form-check form-switch">
          <input
            className="form-check-input"
            type="checkbox"
            role="switch"
            id="flexSwitchCheckDefault"
            onChange={onExcelModeChange}
            checked={excelMode}
          />
          <label
            className="form-check-label"
            htmlFor="flexSwitchCheckDefault"
          >
            Registrar desde Excel
          </label>
        </div>
      )}
      <hr />
      {!excelMode && (
        <>
          <div className="form-group">
            <label>Cantidad:</label>
            <input
              id="quantity"
              name="quantity"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
              className={`form-control ${quantityError && "error"}`}
              placeholder="Cantidad"
            />
            {quantityError && (
              <span className="error-message">{quantityError}</span>
            )}
          </div>
          <div className="form-group">
            <label>Producto:</label>
            <select
              id="product_id"
              quantity="product_id"
              value={product_id}
              onChange={onProductIdChange}
              className={`form-control ${productError && "error"}`}
            >
              <option value="">--Seleccione un Producto--</option>
              {products.map((product) => (
                <option key={product.id} value={product.id}>
                  {product.name}
                </option>
              ))}
            </select>
            {productError && (
              <span className="error-message">{productError}</span>
            )}
          </div>
        </>
      )}
      {/* Mostrar cuando se quiere registrar por medio de excel */}
      {excelMode && (
        <div className={`form-group`}>
          <div className="mb-3">
            <label>Subir Excel de SubCategorias:</label>
            <input
              className="form-control form-control-sm"
              id="formFileSm"
              type="file"
              accept=".xlsx,.xls"
              onChange={onFileChange}
            />
            {fileError && (
              <span className="error-message">{fileError}</span>
            )}
          </div>
        </div>
      )}
      <div className="containerButton">
        <button type="button" className="btns danger" onClick={closeModal}>
          Cancelar <i className="fa-solid fa-xmark"></i>
        </button>
        <button
          type="button"
          className="btns success"
          onClick={saveOrUpdate}
        >
          Guardar <i className="fa-solid fa-check"></i>
        </button>
      </div>
    </form>
  );
}

export { EntryForm };
