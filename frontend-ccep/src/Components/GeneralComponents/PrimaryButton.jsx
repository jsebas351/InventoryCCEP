import React from "react";
import "../../Styles/GeneralStyles/PrimaryButton.css";

function PrimaryButton({ icon, text, title, execute }) {

  return (
    <button onClick={execute} className="primary-button" title={title}>
      <i className={icon}></i>
      &nbsp;{text}
    </button>
  );
}

export { PrimaryButton };