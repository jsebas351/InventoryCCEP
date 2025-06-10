import React, { useContext, useEffect, useState } from "react";
import { DangerButton } from "../../GeneralComponents/DangerButton";
import { PrimaryButton } from "../../GeneralComponents/PrimaryButton";
import { GeneralContext } from "../../../Context/GeneralContext";
import ServiceSale from "../../../Services/ServiceSale";

function SummarySale({
  selectedProducts,
  onRemoveProduct,
  onRemoveAllProducts,
}) {
  const [quantities, setQuantities] = useState({});
  const [discounts, setDiscounts] = useState({});
  const [total, setTotal] = useState(0);
  const [paymentsMethods, setPaymentsMethods] = useState([]);
  const [amountCash, setAmountCash] = useState(0);

  const [remainingCash, setRemainingCash] = useState(0);

  // Estado para controlar la habilitación del input de descuento
  const [isDiscountEnabled, setIsDiscountEnabled] = useState(false);

  //Contexto General
  const { ok, swalCard } = useContext(GeneralContext);

  // Campos para llenar el json
  const [discount, setDiscount] = useState("");
  const [paymethod_id, setPaymethodId] = useState(0);

  useEffect(() => {
    // Calcula el subtotal de cada producto y el total de la venta
    let subtotal = 0;
    selectedProducts.forEach((product) => {
      const quantity = quantities[product.id] || 0;
      const discountUnity = discounts[product.id] || 0;
      const productSubtotal = quantity * product.sale_price;
      subtotal -= discountUnity;
      subtotal += productSubtotal;
    });
    // Aplica el descuento al subtotal
    const discountedTotal = subtotal - discount;
    setTotal(discountedTotal > 0 ? discountedTotal : 0);
  }, [selectedProducts, quantities, discounts, discount]);

  // Listar los métodos de pago
  const paymentsMethodsList = () => {
    ServiceSale.getAllPaymentsMethods()
      .then((response) => {
        setPaymentsMethods(response.data.data);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    paymentsMethodsList();
  }, []);

  // Función para manejar cambios en la habilitación del input de descuento
  const handleDiscountCheckboxChange = (e) => {
    const isChecked = e.target.checked;
    setIsDiscountEnabled(isChecked); 

    // Verifica si el campo de descuento tiene un valor y si el checkbox está marcado
    if (!isChecked) {
      setDiscount(""); 
    }
  };

  // Función para manejar cambios en la cantidad de un producto
  const handleQuantityChange = (productId, quantity) => {
    if (quantity >= 0 && quantity >= "") {
      setQuantities((prevQuantities) => ({
        ...prevQuantities,
        [productId]: quantity,
      }));
    }
  };

  // Función para manejar cambios en el descuento de un producto
  const handleDiscountChange = (productId, discount) => {
    if (discount >= 0 && discount >= "") {
      setDiscounts((prevDiscounts) => ({
        ...prevDiscounts,
        [productId]: discount,
      }));
    }
  };

  // Función para manejar cambios en el descuento total de la venta
  const handleDiscountTotalChange = (discount) => {
    if (discount >= 0 && discount >= "") {
      setDiscount(discount);
    }
  };

  // Funcion para manejar la cantidad en efectivo para que no sea menor al total de la venta
  const handleAmountCashChange = (amountCash) => {
    if (amountCash >= 0 && amountCash >= "") {
      setAmountCash(amountCash);
    }

    // Calcula el restante del efectivo entregado por el cliente
    if(amountCash >= total) {
     const remainingCash = amountCash - total;
     setRemainingCash(remainingCash);
    }
  };

  // Función para eliminar un producto de la lista
  const handleRemoveProduct = (product) => {
    onRemoveProduct(product);
    // Elimina la cantidad del producto del estado de cantidades
    setQuantities((prevQuantities) => {
      const newQuantities = { ...prevQuantities };
      delete newQuantities[product.id];
      return newQuantities;
    });
  };

  // Función para limpiar los estados después de una venta exitosa
  const resetComponentState = () => {
    setQuantities({});
    setDiscounts({});
    setTotal(0);
    setDiscount(0);
    setPaymethodId(0);
    setAmountCash(0);
    setIsDiscountEnabled(false);
    onRemoveAllProducts();
  };

  // Función para guardar la venta en el servidor
  const saveSale = () => {
    const details = selectedProducts.map((product) => ({
      quantity: quantities[product.id] || 0,
      product_id: product.id,
      discount_product: discounts[product.id] || 0,
    }));

    const saleData = {
      paymethod_id: parseInt(paymethod_id),
      discount: parseInt(discount) ? parseInt(discount) : 0,
      user_id: 1, // Aquí debes establecer el ID del usuario actual
      details: details,
    };

    ServiceSale.saveSaleWithDetails(saleData)
      .then((response) => {
        if (response.data.code == 400) {
          swalCard("Error al Guardar la Venta", response.data.message, "info");
        } else if (response.data.code == 404) {
          swalCard("No se encontro", response.data.message, "info");
        } else if (response.data.code == 500) {
          swalCard("Error al Guardar la Venta", response.data.message, "error");
        } else {
          ok(response.data.message, "success");
          resetComponentState();
        }
      })
      .catch((error) => {
        console.error("Error al guardar la venta:", error);
      });
  };

  return (
    <div>
      <h2>Resumen de la Venta</h2>
      <br />
      <div className="scrollable-summary">
        <div className="table-responsive">
          <table className="table caption-top align-items-center">
            <caption>Detalles</caption>
            <thead>
              <tr>
                <th scope="col">Nombre</th>
                <th scope="col">Precio</th>
                <th scope="col">Cantidad</th>
                <th scope="col">Descuento</th>
                <th scope="col">Subtotal</th>
                <th scope="col">Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {selectedProducts.map((product) => (
                <tr key={product.id}>
                  <td className="product-name" title={product.name}>
                    {product.name}
                  </td>
                  <td>${product.sale_price.toLocaleString("es-CO")}</td>
                  <td>
                    <input
                      type="number"
                      className="form-control width-quantity"
                      value={quantities[product.id] || ""}
                      onChange={(e) =>
                        handleQuantityChange(
                          product.id,
                          parseInt(e.target.value)
                        )
                      }
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      className="form-control width-quantity"
                      value={discounts[product.id] || ""}
                      onChange={(e) =>
                        handleDiscountChange(
                          product.id,
                          parseInt(e.target.value)
                        )
                      }
                    />
                  </td>
                  {/* Validacion de no mostrar el subtotal negativo */}
                  <td>
                    $
                    {((quantities[product.id] || 0) * product.sale_price -
                      (discounts[product.id] || 0) >
                    0
                      ? (quantities[product.id] || 0) * product.sale_price -
                        (discounts[product.id] || 0)
                      : 0
                    ).toLocaleString("es-CO")}
                  </td>
                  <td>
                    <DangerButton
                      icon={"fa-regular fa-trash-can"}
                      execute={() => handleRemoveProduct(product)}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
      <br />
      <div className="card">
        <div className="card-body">
          <h5 className="card-title">Finalizar Venta</h5>
          <div className="row">
            <div className="col-md-6">
              <label>Metodo de Pago:</label>
              <select
                className="form-select"
                name="paymethod_id"
                id="paymethod_id"
                value={paymethod_id}
                onChange={(e) => setPaymethodId(e.target.value)}
              >
                <option value="">Seleccione una Opcion</option>
                {paymentsMethods.map((paymentmethod) => (
                  <option key={paymentmethod.id} value={paymentmethod.id}>
                    {paymentmethod.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="col-md-6">
              <label>Descuento: </label>
              <div className="input-group mb-3">
                <div className="input-group-text">
                  <input
                    className="form-check-input mt-0"
                    type="checkbox"
                    value=""
                    aria-label="Checkbox for following text input"
                    onChange={handleDiscountCheckboxChange}
                    checked={isDiscountEnabled}
                  />
                </div>
                <input
                  type="number"
                  className="form-control"
                  aria-label="Text input with checkbox"
                  value={discount}
                  onChange={(e) => handleDiscountTotalChange(e.target.value)}
                  disabled={!isDiscountEnabled}
                />
                <span className="input-group-text">COP</span>
              </div>
            </div>
            {/* Vueltas */}
            <div className="col-md-6">
              <label>Entrega de Efectivo: </label>
              
                <input
                  type="number"
                  className="form-control"
                  aria-label="Text input with checkbox"
                  value={amountCash}
                  onChange={(e) => handleAmountCashChange(e.target.value)}
                />

            </div>
            <div className="col-md-6">
                <label>Restante: </label>
                <span className="input-group-text">${remainingCash.toLocaleString("es-CO")} COP</span> 
            </div>
          </div>
          <br />
          <p className="card-text">Total: ${total.toLocaleString("es-CO")}</p>
          <PrimaryButton
            text={"Finalizar"}
            icon={"fa-solid fa-check"}
            execute={saveSale}
          />
        </div>
      </div>
    </div>
  );
}

export { SummarySale };
