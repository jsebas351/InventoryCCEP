import React, { useContext, useEffect, useState } from "react";
import { GeneralContext } from "../../Context/GeneralContext";
import ServiceProduct from "../../Services/ServiceProduct";
import ServiceSubCategory from "../../Services/ServiceSubCategory";
import ServiceSupplier from "../../Services/ServiceSupplier";
import "../../Styles/Products/ProductForm.css";

function ProductForm({ productsList, editProduct }) {
  //Campos del json y del formulario
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [purchase_price, setPurchase_price] = useState("");
  const [sale_price, setSale_price] = useState("");
  const [subcategory_id, setSubcategory_id] = useState("");
  const [provider_id, setProvider_id] = useState("");
  const [state, setState] = useState("Activo");

  //Estado para saber si se quiere agregar Proveedores con un excel
  const [excelMode, setExcelMode] = useState(false);
  const [excelProducts, setExcelProducts] = useState(null);

  //Texto para mostrar los errores de validacion
  const [nameError, setNameError] = useState("");
  const [descriptionError, setDescriptionError] = useState("");
  const [purchase_priceError, setPurchase_priceError] = useState("");
  const [sale_priceError, setSale_priceError] = useState("");
  const [subcategory_idError, setSubcategory_idError] = useState("");
  const [provider_idError, setProvider_idError] = useState("");
  const [stateError, setStateError] = useState("");
  const [fileError, setFileError] = useState("");

  //Estados para listar el select
  const [subCategories, setSubCategories] = useState([]);
  const [suppliers, setSuppliers] = useState([]);

  //Estado para saber si se esta editando o no
  const [isEditMode, setIsEditMode] = useState(false);

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
        setExcelProducts(null);
        return;
      }

      // Validar el tamaño del archivo
      const maxFileSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxFileSize) {
        setFileError(
          "El archivo es demasiado grande. El tamaño máximo permitido es 5MB."
        );
        setExcelProducts(null);
        return;
      }

      setFileError("");
      setExcelProducts(file);
    }
  };

  const onExcelModeChange = (e) => {
    setExcelMode(e.target.checked);
    setFileError("");
    setExcelProducts(null);
  };

  const onSubCategoryIdChange = (e) => {
    setSubcategory_id(e.target.value);
  };

  const onProviderIdChange = (e) => {
    setProvider_id(e.target.value);
  };

  //Llamado al service para listar SubCategorias una vez terminada la operacion de editar o crear
  const subCategoriesList = () => {
    ServiceSubCategory.getAllNotPaginated()
      .then((response) => {
        setSubCategories(response.data.data);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  //Llamado al service para listar proveedores una vez terminada la operacion de editar o crear
  const suppliersList = () => {
    ServiceSupplier.getAllSuppliersNotPaginated()
      .then((response) => {
        setSuppliers(response.data.data);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    subCategoriesList();
    suppliersList();
  }, []);

  useEffect(() => {
    //Si se proporciona un proveedor, establecer el modo de edicion y llenar los campos
    if (editProduct) {
      setIsEditMode(true);
      setName(editProduct.name);
      setDescription(editProduct.description);
      setPurchase_price(editProduct.purchase_price);
      setSale_price(editProduct.sale_price);
      setSubcategory_id(editProduct.subcategory_id.id);
      setProvider_id(editProduct.provider_id.id);
      setState(editProduct.state);
    } else {
      setIsEditMode(false);
      setName("");
      setDescription("");
      setPurchase_price("");
      setSale_price("");
      setSubcategory_id("");
      setProvider_id("Activo");
    }
  }, [editProduct]);

  const saveOrUpdate = async (e) => {
    e.preventDefault();

    const product = {
      name,
      description,
      purchase_price,
      sale_price,
      subcategory_id,
      provider_id,
      state,
    };

    if (!excelMode) {
      //Validacion de campos
      if (name.trim() === "") {
        setNameError("Este campo es requerido");
        return;
      } else {
        setNameError("");
      }

      if (description.trim() === "") {
        setDescriptionError("Este campo es requerido");
        return;
      } else if (description.length === 254) {
        setDescriptionError("La descripcion no puede ser tan extensa");
      } else {
        setNameError("");
      }

      if (purchase_price.toString().trim() === "") {
        setPurchase_priceError("Este campo es requerido");
        return;
      } else {
        setPurchase_priceError("");
      }

      if (sale_price.toString().trim() === "") {
        setSale_priceError("Este campo es requerido");
        return;
      } else {
        setSale_priceError("");
      }

      if (subcategory_id.toString().trim() === "") {
        setSubcategory_idError("Este campo es requerido");
        return;
      } else {
        setSubcategory_idError("");
      }

      if (provider_id.toString().trim() === "") {
        setProvider_idError("Este campo es requerido");
        return;
      } else {
        setProvider_idError("");
      }

      if (state.trim() === "") {
        setStateError("Este campo es requerido");
        return;
      } else {
        setStateError("");
      }
    }
    try {
      if (isEditMode) {
        const response = await ServiceProduct.update(editProduct.id, product);
        ok(response.data.message, "success");

        if (response.data.code == 400) {
          swalCard("Producto Existente", response.data.message, "error");
        }
      } else if (excelMode) {
        if (!excelProducts) {
          setFileError("Debes seleccionar un archivo Excel para continuar.");
          return;
        }

        const response = await ServiceProduct.saveProductsExcel(
          excelProducts
        );
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
        const response = await ServiceProduct.add(product);
        ok(response.data.message, "success");

        if (response.data.code == 400) {
          swalCard("Producto Existente", response.data.message, "error");
        }
      }

      closeModal();
      productsList();
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
          <label className="form-check-label" htmlFor="flexSwitchCheckDefault">
            Registrar desde Excel
          </label>
        </div>
      )}
      <hr />
      {!excelMode && (
        <>
          <div className="form-group">
            <label htmlFor="name">Nombre:</label>
            <input
              type="text"
              className={`form-control ${nameError && "error"}`}
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
            {nameError && <div className="text-danger">{nameError}</div>}
          </div>
          <div className="row">
            <div className="form-group col-md-6">
              <label htmlFor="purchase_price">Precio de Compra:</label>
              <input
                type="number"
                className={`form-control ${purchase_priceError && "error"}`}
                id="purchase_price"
                value={purchase_price}
                onChange={(e) => setPurchase_price(e.target.value)}
              />
              {purchase_priceError && (
                <div className="text-danger">{purchase_priceError}</div>
              )}
            </div>
            <div className="form-group col-md-6">
              <label htmlFor="sale_price">Precio de Venta:</label>
              <input
                type="number"
                className={`form-control ${sale_priceError && "error"}`}
                id="sale_price"
                value={sale_price}
                onChange={(e) => setSale_price(e.target.value)}
              />
              {sale_priceError && (
                <div className="text-danger">{sale_priceError}</div>
              )}
            </div>
          </div>
          <div className="form-group">
            <label htmlFor="description">Descripción:</label>
            <textarea
              className={`form-control ${descriptionError && "error"}`}
              id="description"
              rows="3"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
            {descriptionError && (
              <div className="text-danger">{descriptionError}</div>
            )}
          </div>
          <div className="row">
            <div className="form-group col-md-6">
              <label htmlFor="subcategory_id">Subcategoría:</label>
              <select
                className={`form-control ${subcategory_idError && "error"}`}
                id="subcategory_id"
                value={subcategory_id}
                onChange={onSubCategoryIdChange}
              >
                <option value="">Seleccione una opcion</option>
                {subCategories.map((subcategory) => (
                  <option key={subcategory.id} value={subcategory.id}>
                    {subcategory.name}
                  </option>
                ))}
              </select>
              {subcategory_idError && (
                <div className="text-danger">{subcategory_idError}</div>
              )}
            </div>
            <div className="form-group col-md-6">
              <label htmlFor="provider_id">Proveedor:</label>
              <select
                className={`form-control ${provider_idError && "error"}`}
                id="provider_id"
                value={provider_id}
                onChange={onProviderIdChange}
              >
                <option value="">Seleccione una opcion</option>
                {suppliers.map((supplier) => (
                  <option key={supplier.id} value={supplier.id}>
                    {supplier.name}
                  </option>
                ))}
              </select>
              {provider_idError && (
                <div className="text-danger">{provider_idError}</div>
              )}
            </div>
          </div>
        </>
      )}
      {/* Mostrar cada vez que se quiera editar */}
      {isEditMode && !excelMode && (
        <div className="form-group">
          <label>Estado:</label>
          <select
            id="state"
            name="state"
            value={state}
            onChange={onStatusChange}
            className={`form-control ${stateError && "error"}`}
          >
            <option value="Activo">Activo</option>
            <option value="Inactivo">Inactivo</option>
          </select>
          {stateError && <span className="error-message">{stateError}</span>}
        </div>
      )}
      {/* Mostrar cuando se quiere registrar por medio de excel */}
      {excelMode && (
        <div className={`form-group`}>
          <div className="mb-3">
            <label>Subir Excel de Productos:</label>
            <input
              className="form-control form-control-sm"
              id="formFileSm"
              type="file"
              accept=".xlsx,.xls"
              onChange={onFileChange}
            />
            {fileError && <span className="error-message">{fileError}</span>}
          </div>
        </div>
      )}
      <div className="containerButton">
        <button type="button" className="btns danger" onClick={closeModal}>
          Cancelar <i className="fa-solid fa-xmark"></i>
        </button>
        <button type="button" className="btns success" onClick={saveOrUpdate}>
          Guardar <i className="fa-solid fa-check"></i>
        </button>
      </div>
    </form>
  );
}

export { ProductForm };
