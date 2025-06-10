import React from "react";
import "../../Styles/GeneralStyles/WarningButton.css";

function WarningButton({ text, icon, title,execute }) {
  return (
    <button onClick={execute} className="warning-button" title={title}>
      <i className={icon}></i>
      {text}
    </button>
  );
}

export { WarningButton };