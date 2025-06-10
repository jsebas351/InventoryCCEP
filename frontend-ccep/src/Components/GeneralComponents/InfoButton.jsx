import React from "react";
import "../../Styles/GeneralStyles/InfoButton.css";

function InfoButton({ text, icon, execute }) {
  return (
    <button onClick={execute} className="info-button">
      <i className={icon}></i>
      {text}
    </button>
  );
}

export { InfoButton };