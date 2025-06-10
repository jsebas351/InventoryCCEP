import React, { useContext, useEffect, useState } from "react";
import "../../Styles/Suppliers/SupplierForm.css";
import ServiceSupplier from "../../Services/ServiceSupplier";
import { GeneralContext } from "../../Context/GeneralContext";

function SupplierForm({ suppliersList, editSupplier }) {
  //Campos del json y del formulario
  const [nit, setNit] = useState("");
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [mail, setMail] = useState("");
  const [state, setState] = useState("Activo");

  //Estado para saber si se quiere agregar Proveedores con un excel
  const [excelMode, setExcelMode] = useState(false);
  const [excelSuppliers, setExcelSuppliers] = useState(null);

  //Texto para mostrar los errores de validacion
  const [nitError, setNitError] = useState("");
  const [nameError, setNameError] = useState("");
  const [phoneError, setPhoneError] = useState("");
  const [mailError, setMailError] = useState("");
  const [stateError, setStateError] = useState("");
  const [fileError, setFileError] = useState("");

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
        setExcelSuppliers(null);
        return;
      }

      // Validar el tamaño del archivo
      const maxFileSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxFileSize) {
        setFileError(
          "El archivo es demasiado grande. El tamaño máximo permitido es 5MB."
        );
        setExcelSuppliers(null);
        return;
      }

      setFileError("");
      setExcelSuppliers(file);
    }
  };

  const onExcelModeChange = (e) => {
    setExcelMode(e.target.checked);
    setFileError("");
    setExcelSuppliers(null);
  };

  //Funcion para actualizar el estado de edicion y llenar los campos si se esta editando
  useEffect(() => {
    //Si se proporciona un proveedor, establecer el modo de edicion y llenar los campos
    if (editSupplier) {
      setIsEditMode(true);
      setNit(editSupplier.nit);
      setName(editSupplier.name);
      setPhone(editSupplier.phone);
      setMail(editSupplier.mail);
      setState(editSupplier.state);
    } else {
      //Si no se proporciona un proveedor para editar, se resetea el formulario
      setIsEditMode(false);
      setNit("");
      setName("");
      setPhone("");
      setMail("");
      setState("Activo");
    }
  }, [editSupplier]);

  //Metodo para editar o crear Proveedores
  const saveOrUpdate = async (e) => {
    e.preventDefault();

    if (!excelMode) {
      // Validación de campo requerido: NIT
      if (nit.toString().trim() === "") {
        setNitError("Este campo es requerido");
        return;
      } else if (!/^\d{10}$/.test(nit)) {
        // Validación de longitud de NIT
        setNitError("El NIT debe tener 10 dígitos");
        return;
      } else {
        setNitError("");
      }

      //Validacion para el campo nombre
      if (name.trim() === "") {
        setNameError("Este campo es requerido");
        return;
      } else {
        setNameError("");
      }

      // Validación de campo requerido: Teléfono
      if (phone.toString().trim() === "") {
        setPhoneError("Este campo es requerido");
        return;
      } else if (!/^\d{10}$/.test(phone)) {
        // Validación de formato de teléfono
        setPhoneError("El teléfono debe tener 10 dígitos");
        return;
      } else if (
        !phone.toString().startsWith("3") &&
        !phone.toString().startsWith("6")
      ) {
        // Validación de primer dígito
        setPhoneError("El teléfono debe comenzar con 3 o 6");
        return;
      } else {
        setPhoneError("");
      }

      // Validación de campo requerido: Correo electrónico
      if (mail.trim() === "") {
        setMailError("Este campo es requerido");
        return;
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(mail)) {
        // Validación de formato de correo electrónico
        setMailError("Correo electrónico no válido");
        return;
      } else {
        setMailError("");
      }

      //Validar el campo state
      if (state.trim() === "") {
        setStateError("Este campo es requerido");
        return;
      } else {
        setStateError("");
      }
    }

    //Campos del json para enviar el servidor
    const supplier = { nit, name, phone, mail, state };

    try {
      //Validar si se quiere crear o editar
      if (isEditMode) {
        const response = await ServiceSupplier.update(
          editSupplier.id,
          supplier
        );
        ok(response.data.message, "success");

        //Validacion el Backend
        if (response.data.code == 400) {
          swalCard("Proveedor Existente", response.data.message, "error");
        }
      } else if (excelMode) {
        if (!excelSuppliers) {
          setFileError("Debes seleccionar un archivo Excel para continuar.");
          return;
        }

        const response = await ServiceSupplier.saveSuppliersExcel(
          excelSuppliers
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
        const response = await ServiceSupplier.add(supplier);
        ok(response.data.message, "success");

        //Validacion el Backend
        if (response.data.code == 400) {
          swalCard("Proveedor Existente", response.data.message, "error");
        }
      }

      closeModal();
      suppliersList();
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
        <>
          <div className="form-group">
            <label>Nit:</label>
            <input
              id="nit"
              name="nit"
              type="number"
              value={nit}
              onChange={(e) => setNit(e.target.value)}
              className={`form-control ${nitError && "error"}`}
              placeholder="Ingrese el Nit"
            />
            {nitError && <span className="error-message">{nitError}</span>}
          </div>

          <div className="form-group">
            <label>Nombre:</label>
            <input
              id="name"
              name="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className={`form-control ${nameError && "error"}`}
              placeholder="Ingrese el Nombre"
            />
            {nameError && <span className="error-message">{nameError}</span>}
          </div>
          <div className="form-group">
            <label>Telefono:</label>
            <input
              id="phone"
              name="phone"
              type="number"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              className={`form-control ${phoneError && "error"}`}
              placeholder="Ingrese el Telefono"
            />
            {phoneError && <span className="error-message">{phoneError}</span>}
          </div>
          <div className="form-group">
            <label>Correo:</label>
            <input
              id="mail"
              name="mail"
              type="email"
              value={mail}
              onChange={(e) => setMail(e.target.value)}
              className={`form-control ${mailError && "error"}`}
              placeholder="Ingrese el Correo"
            />
            {mailError && <span className="error-message">{mailError}</span>}
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
            <label>Subir Excel de Proveedores:</label>
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

export { SupplierForm };
