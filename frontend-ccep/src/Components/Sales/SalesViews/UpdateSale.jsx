import React, { useState, useEffect, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Select from "react-select";
import { DangerButton } from "../../GeneralComponents/DangerButton";
import { PrimaryButton } from "../../GeneralComponents/PrimaryButton";
import ServiceSale from "../../../Services/ServiceSale";
import ServiceProduct from "../../../Services/ServiceProduct";
import { GeneralContext } from "../../../Context/GeneralContext";

function UpdateSale() {
  const [quantities, setQuantities] = useState({});
  const [discounts, setDiscounts] = useState({});
  const [discount, setDiscount] = useState(0);
  const [total, setTotal] = useState(0);
  const [paymentMethod, setPaymentMethod] = useState("");
  const [status, setStatus] = useState("");
  const [user, setUser] = useState(0);
  const [sale_date, setSaleDate] = useState("");
  const [productDetails, setProductDetails] = useState([]);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [products, setProducts] = useState([]);
  const [sale, setSale] = useState(null);
  const [highlightedProduct, setHighlightedProduct] = useState(null);
  const navigate = useNavigate();

  const { id } = useParams();
  const { ok, swalCard } = useContext(GeneralContext);

  // Función para resaltar un producto
  const highlightProduct = (productId) => {
    setHighlightedProduct(productId);
    setTimeout(() => setHighlightedProduct(null), 2000); // Quitar el resaltado después de 2 segundos
  };

  // Listar los métodos de pago
  const getPaymentsMethods = async () => {
    try {
      const response = await ServiceSale.getAllPaymentsMethods();
      const paymentsMethods = response.data.data;
      setPaymentMethods(paymentsMethods);
    } catch (error) {
      console.log("Error Cargando los datos: ", error);
    }
  };

  // Obtener los detalles de la venta
  const getSaleDetails = async (id) => {
    try {
      const response = await ServiceSale.getSaleDetailById(id);
      const saleDetails = response.data.data;
      setProductDetails(saleDetails);
      const quantities = {};
      const discounts = {};
      saleDetails.forEach((detail) => {
        quantities[detail.product_id.id] = detail.quantity;
        discounts[detail.product_id.id] = detail.discount_product;
      });
      setQuantities(quantities);
      setDiscounts(discounts);
    } catch (error) {
      console.log("Error Cargando los detalles de la venta: ", error);
    }
  };

  // Obtener todos los productos
  const getAllProducts = async () => {
    try {
      const response = await ServiceProduct.getAllProductsNotPaginated();
      const productsList = response.data.data;
      setProducts(productsList);
    } catch (error) {
      console.log("Error Cargando los productos: ", error);
    }
  };

  // Llenar los campos con la venta encontrada
  const getSaleToEdit = async (id) => {
    try {
      const response = await ServiceSale.getSaleById(id);
      const saleFind = response.data.data;
      setSale(saleFind);
    } catch (error) {
      console.log("Error Cargando los datos: ", error);
    }
  };

  useEffect(() => {
    getPaymentsMethods();
    getSaleToEdit(id);
    getSaleDetails(id);
    getAllProducts();
  }, [id]);

  useEffect(() => {
    if (sale) {
      setUser(sale.user_id);
      setPaymentMethod(sale.paymethod_id.id);
      setStatus(sale.state);
      setTotal(sale.total_sale);
      setDiscount(sale.discount);
      setSaleDate(sale.sale_date);
    }
  }, [sale]);

  useEffect(() => {
    let subtotal = 0;
    productDetails.forEach((product) => {
      const quantity = quantities[product.product_id.id] || 0;
      const discountUnity = discounts[product.product_id.id] || 0;
      const productSubtotal = quantity * product.product_id.sale_price;
      subtotal += productSubtotal;
      subtotal -= discountUnity;
    });
    const discountedTotal = subtotal - discount;
    setTotal(discountedTotal > 0 ? discountedTotal : 0);
  }, [productDetails, quantities, discounts, discount]);

  const handleQuantityChange = (productId, quantity) => {
    if (quantity >= 0) {
      setQuantities((prevQuantities) => ({
        ...prevQuantities,
        [productId]: quantity,
      }));
    }
  };

  const handleDiscountChange = (productId, discount) => {
    if (discount >= 0) {
      setDiscounts((prevDiscounts) => ({
        ...prevDiscounts,
        [productId]: discount,
      }));
    }
  };

  const handleDiscountTotalChange = (discount) => {
    if (discount >= 0) {
      setDiscount(discount);
    }
  };

  const handleRemoveProduct = (index) => {
    const newDetails = productDetails.filter((_, i) => i !== index);
    setProductDetails(newDetails);
    const newQuantities = { ...quantities };
    delete newQuantities[productDetails[index].product_id.id];
    setQuantities(newQuantities);
    const newDiscounts = { ...discounts };
    delete newDiscounts[productDetails[index].product_id.id];
    setDiscounts(newDiscounts);
  };

  const saveSale = () => {
    const details = productDetails.map((detail) => ({
      id: detail.id || 0,
      quantity: quantities[detail.product_id.id] || 0,
      product_id: detail.product_id.id,
      discount_product: discounts[detail.product_id.id] || 0,
    }));

    const saleData = {
      paymethod_id: parseInt(paymentMethod),
      discount: parseInt(discount) ? parseInt(discount) : 0,
      user_id: user,
      status: status,
      details: details,
    };

    ServiceSale.updateSaleWithDetails(id, saleData)
      .then((response) => {
        if (response.data.code === 400) {
          swalCard(
            "Error al Actualizar la Venta",
            response.data.message,
            "info"
          );
        } else if (response.data.code === 404) {
          swalCard("No se encontró", response.data.message, "info");
        } else if (response.data.code === 500) {
          swalCard(
            "Error al Actualizar la Venta",
            response.data.message,
            "error"
          );
        } else {
          ok(response.data.message, "success");
          navigate(-1);
        }
      })
      .catch((error) => {
        console.error("Error al actualizar la venta:", error);
      });
  };

  return (
    <div className="container mt-5">
      <h1 className="text-center mb-4">Actualizar la Venta</h1>
      <div className="row">
        <div className="col-md-5">
          <div className="card mb-4">
            <div className="card-header d-flex justify-content-between">
              <span>Venta</span>
              <span className="text-muted" style={{ fontSize: "0.80rem" }}>
                {sale_date}
              </span>
            </div>
            <div className="card-body">
              <div className="mb-3">
                <label className="form-label">Total:</label>
                <span className="form-control-plaintext">
                  ${total.toLocaleString("es-CO")}
                </span>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="discount">
                  Descuento:
                </label>
                <input
                  type="number"
                  className="form-control"
                  id="discount"
                  placeholder="Descuento"
                  value={discount}
                  onChange={(e) =>
                    handleDiscountTotalChange(parseInt(e.target.value) || 0)
                  }
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Usuario:</label>
                <span className="form-control-plaintext text-primary">
                  {user}
                </span>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="paymentMethod">
                  Método de Pago:
                </label>
                <select
                  className="form-select"
                  id="paymentMethod"
                  value={paymentMethod}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                >
                  <option value="">Selecciona un método de pago</option>
                  {paymentMethods.map((method) => (
                    <option key={method.id} value={method.id}>
                      {method.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="status">
                  Estado:
                </label>
                <select
                  className="form-select"
                  id="status"
                  value={status}
                  onChange={(e) => setStatus(e.target.value)}
                >
                  <option value="">Selecciona un estado</option>
                  <option value="Activo">Activo</option>
                  <option value="Inactivo">Inactivo</option>
                </select>
              </div>
            </div>
            <div className="card-footer">
              <div className="d-flex justify-content-between">
                <DangerButton execute={() => navigate(-1)} text="Cancelar" />
                <PrimaryButton execute={saveSale} text="Actualizar" />
              </div>
            </div>
          </div>
        </div>
        <div className="col-md-7">
          <div className="card">
            <div className="card-header">Productos</div>
            <div className="card-body">
              <div className="table-responsive">
                <table className="table">
                  <thead>
                    <tr>
                      <th scope="col">Nombre</th>
                      <th scope="col">Precio</th>
                      <th scope="col">Cantidad</th>
                      <th scope="col">Descuento</th>
                      <th scope="col">Subtotal</th>
                      <th scope="col">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {productDetails.map((detail, index) => (
                      <tr
                        key={detail.id}
                        className={
                          highlightedProduct === detail.product_id.id
                            ? "highlighted"
                            : ""
                        }
                      >
                        <td>{detail.product_id.name}</td>
                        <td>
                          $
                          {detail.product_id.sale_price.toLocaleString("es-CO")}
                        </td>
                        <td>
                          <input
                            type="number"
                            className="form-control"
                            value={quantities[detail.product_id.id] || 0}
                            onChange={(e) =>
                              handleQuantityChange(
                                detail.product_id.id,
                                parseInt(e.target.value) || 0
                              )
                            }
                          />
                        </td>
                        <td>
                          <input
                            type="number"
                            className="form-control"
                            value={discounts[detail.product_id.id] || 0}
                            onChange={(e) =>
                              handleDiscountChange(
                                detail.product_id.id,
                                parseInt(e.target.value) || 0
                              )
                            }
                          />
                        </td>
                        {/* Validacion de no mostrar el subtotal negativo */}
                        <td>
                          $
                          {((quantities[detail.product_id.id] || 0) *
                            detail.product_id.sale_price -
                            (discounts[detail.product_id.id] || 0) >
                          0
                            ? (quantities[detail.product_id.id] || 0) *
                                detail.product_id.sale_price -
                              (discounts[detail.product_id.id] || 0)
                            : 0
                          ).toLocaleString("es-CO")}
                        </td>
                        <td>
                          <button
                            className="btn btn-danger"
                            onClick={() => handleRemoveProduct(index)}
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <Select
                options={products.map((product) => ({
                  label: product.name,
                  value: product.id,
                }))}
                onChange={(selectedOption) => {
                  const selectedProduct = products.find(
                    (product) => product.id === selectedOption.value
                  );

                  // Verifica si el producto ya está en productDetails
                  const alreadyAdded = productDetails.some(
                    (detail) => detail.product_id.id === selectedProduct.id
                  );

                  if (alreadyAdded) {
                    swalCard(
                      "Producto Duplicado",
                      "Este producto ya está en la lista de detalles.",
                      "info"
                    );
                    highlightProduct(selectedProduct.id); // Resaltar el producto existente
                  } else {
                    setProductDetails([
                      ...productDetails,
                      {
                        product_id: selectedProduct,
                        quantity: 1,
                        discount_product: 0,
                      },
                    ]);
                    setQuantities({ ...quantities, [selectedProduct.id]: 1 });
                    setDiscounts({ ...discounts, [selectedProduct.id]: 0 });
                  }
                }}
                placeholder="Buscar y añadir producto..."
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export { UpdateSale };
