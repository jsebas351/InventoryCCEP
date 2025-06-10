import React, { useContext, useEffect } from "react";
import { GeneralContext } from "../../Context/GeneralContext";
import ReactDOM from "react-dom";
import "../../Styles/GeneralStyles/Modal.css";

function Modal({ children, tittle }) {
  const { modalClasses, closeModal } = useContext(GeneralContext);

  useEffect(() => {
    function handleClickOutside(event) {
      const modalContent = document.querySelector(".ModalContent");
      if (modalContent && !modalContent.contains(event.target)) {
        closeModal();
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [closeModal]);

  return ReactDOM.createPortal(
    <div className="Modal">
      <div className={modalClasses.join(" ")}>
        <button className="CloseButton" onClick={closeModal}>
          &times;
        </button>
        <div className="ModalHeader">
          <h4>{tittle}</h4>
        </div>
        <div className="ModalCard">{children}</div>
      </div>
    </div>,
    document.getElementById("modal")
  );
}

export { Modal };
