import React, { useState } from "react";
import "../Styles/Login/Login.css";
import logo from "../assets/Images/Logo_Ccep.jpg";
import ServiceAuth from "../Services/ServiceAuth";
import ServiceUser from "../Services/ServiceUser";
import { PrimaryButton } from "../Components/GeneralComponents/PrimaryButton";
import Swal from "sweetalert2";
import { useNavigate } from "react-router-dom";
import Cookies from "js-cookie"; // Importa la biblioteca

function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

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

  const swalCard = (title, msj, icon) => {
    Swal.fire({
      title: title,
      text: msj,
      icon: icon,
      confirmButtonText: "Entendido",
    });
  };

  const login = async (event) => {
    event.preventDefault();
    sessionStorage.removeItem("authToken");
    try {
      const login = { email, password };
      const response = await ServiceAuth.authentication(login);

      const token =
        response.headers.get("Authorization") ||
        response.headers.get("authorization");

      if (token) {

        Cookies.set("authToken", token, {
          secure: true,
          sameSite: "Strict",
          expires: 2 / 24, // 2 horas
        });

        ok(response.data.message, "success");

        // Obtener usuario por email
        const user = await ServiceUser.findByEmail(email);

        // Guardar usuario en una cookie segura
        Cookies.set("user", JSON.stringify(user.data.data), {
          secure: true,
          expires: 2 / 24,
        });

        const from = location.state?.from?.pathname || "/dashboard/index";
        navigate(from);
      } else {
        throw new Error("Hubo un error, intentelo nuevamente");
      }
    } catch (error) {
      console.log("error: " + error);
      swalCard(
        "Credenciales Incorrectas",
        "Email o contraseña incorrectos",
        "error"
      );
    }
  };

  return (
    <div className="container position-relative divCard">
      <div className="position-absolute start-50 translate-middle border">
        <div id="form">
          <div className="imgcontainer">
            <img src={logo} alt="Avatar" className="avatar" />
          </div>

          <form className="containerLogin" onSubmit={login}>
            <label htmlFor="uname">
              <b>Correo Electrónico: </b>
            </label>
            <input
              type="text"
              placeholder="Ingrese el Correo Electrónico"
              onChange={(e) => setEmail(e.target.value)}
              name="email"
              value={email}
              required
            />

            <label htmlFor="psw">
              <b>Contraseña: </b>
            </label>
            <input
              type="password"
              placeholder="Ingrese la contraseña"
              onChange={(e) => setPassword(e.target.value)}
              name="password"
              value={password}
              required
            />
            <br />
            <br />
            <div className="text-center">
              <PrimaryButton
                text={"Iniciar Sesión"}
                icon={"fa-solid fa-arrow-right-to-bracket"}
                type="submit"
              />
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export { Login };
