import React from "react";
import { NavLink } from "react-router-dom";
import "../../Styles/SideBar/SideBarLink.css"

function SideBarLink({ to, text, icon }) {
  return (
    <nav id="nav">
      <NavLink to={to} id="a">
        <i className={icon}></i>
        <span className="links_name">{text}</span>
      </NavLink>
      <span className="tooltip"> {text} </span>
    </nav>
  );
}

export { SideBarLink };