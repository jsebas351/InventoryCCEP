import React, { useContext, useEffect, useState } from "react";
import "../../Styles/Categories/CategoryForm.css";
import ServiceCategory from "../../Services/ServiceCategory";
import { GeneralContext } from "../../Context/GeneralContext";

function CategoryForm({ categoriesList, editCategory }) {
  //Campos del json y del formulario
  const [name, setName] = useState("");
  const [state, setState] = useState("Activo");

  //Estado para saber si se esta editando o no
  const [isEditMode, setIsEditMode] = useState(false);

  //Estado para saber si se quiere agregar categorias con un excel
  const [excelMode, setExcelMode] = useState(false);
  const [excelCategories, setExcelCategories] = useState(null);

  //Texto para mostrar errores de validaciones
  const [nameError, setNameError] = useState("");
  const [stateError, setStateError] = useState("");
  const [fileError, setFileError] = useState("");

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
        setExcelCategories(null);
        return;
      }

      // Validar el tamaño del archivo
      const maxFileSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxFileSize) {
        setFileError(
          "El archivo es demasiado grande. El tamaño máximo permitido es 5MB."
        );
        setExcelCategories(null);
        return;
      }

      setFileError("");
      setExcelCategories(file);
    }
  };

  const onExcelModeChange = (e) => {
    setExcelMode(e.target.checked);
    setFileError("");
    setExcelCategories(null);
  };

  useEffect(() => {
    // Si se proporciona una categoría para editar, establecer el modo de edición y llenar los campos.
    if (editCategory) {
      setIsEditMode(true);
      setName(editCategory.name);
      setState(editCategory.state);
    } else {
      // Si no se proporciona una categoría para editar, resetear el formulario.
      setName("");
      setState("Activo");
      setIsEditMode(false);
    }
  }, [editCategory]);

  //Metodo para editar o crear categorias
  const saveOrUpdate = async (e) => {
    e.preventDefault();

    if (!excelMode) {
      if (name.trim() === "") {
        setNameError("Este campo es requerido");
        return;
      } else {
        setNameError("");
      }

      if (state.trim() === "") {
        setStateError("Este campo es requerido");
        return;
      } else {
        setStateError("");
      }
    }
    //Campos del json para enviar al servidor
    const updatedCategory = { name, state };

    try {
      //Validar si se quiere editar o crear
      if (isEditMode) {
        const response = await ServiceCategory.update(
          editCategory.id,
          updatedCategory
        );
        ok(response.data.message, "success");

        if (response.data.code == 400) {
          swalCard("Categoria Existente", response.data.message, "error");
        }
      } else if (excelMode) {
        if (!excelCategories) {
          setFileError("Debes seleccionar un archivo Excel para continuar.");
          return;
        }

        const response = await ServiceCategory.saveCategoriesExcel(
          excelCategories
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
        const response = await ServiceCategory.add(updatedCategory);
        ok(response.data.message, "success");

        if (response.data.code == 400) {
          swalCard("Categoria Existente", response.data.message, "error");
        }
      }

      closeModal();
      categoriesList();
    } catch (error) {
      console.error(error.message);
      ok(error.message, "error");
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
        <div className="form-group">
          <label>Nombre:</label>
          <input
            id="name"
            name="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className={`form-control ${nameError && "error"}`}
            placeholder="Nombre"
          />
          {nameError && <span className="error-message">{nameError}</span>}
        </div>
      )}
      {isEditMode && !excelMode && (
        <div className="form-group">
          <label>Estado:</label>
          <select
            id="state"
            name="state"
            value={state}
            onChange={onStatusChange}
            className="form-control"
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
            <label>Subir Excel de Categorias:</label>
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

export { CategoryForm };
