import React, { useState } from "react";
import Swal from "sweetalert2";

const GeneralContext = React.createContext();

function GeneralProvider(props) {
  const [openModal, setOpenModal] = useState(false);
  const [closing, setClosing] = useState(false);

  //Estado para cerrar el modal
  const closeModal = () => {
    setClosing(true);
    setTimeout(() => {
      setOpenModal(!openModal);
      setClosing(false);
    }, 300);
  };

  //Alternar la clase closing
  const modalClasses = ["ModalContent"];
  if (closing) {
    modalClasses.push("closing");
  }

  //Alerta de tostada
  const ok = (msj, icon) => {
    Swal.fire({
      title: msj,
      icon: icon,
      showConfirmButton: false,
      timer: 3500,
      timerProgressBar: true,
      position: "bottom-end",
      toast: true,
    });
  };

  //Alerta de carta
  const swalCard = (title, msj, icon) => {
    Swal.fire({
      title: title,
      text: msj,
      icon: icon,
      confirmButtonText: "Entendido",
    });
  };

  return (
    <GeneralContext.Provider
      value={{
        openModal,
        setOpenModal,
        closeModal,
        modalClasses,
        ok,
        swalCard,
      }}
    >
      {props.children}
    </GeneralContext.Provider>
  );
}

export { GeneralProvider, GeneralContext };
