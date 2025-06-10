import React from "react";
import "../../Styles/GeneralStyles/DangerButton.css";

function DangerButton({ text, icon, title, execute }) {
  return (
    <button onClick={execute} className="danger-button" title={title}>
      <i className={icon}></i>
      {text}
    </button>
  );
}

export { DangerButton };