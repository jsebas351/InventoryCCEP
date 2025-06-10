import React, { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { GeneralContext } from "../../Context/GeneralContext";
import ServicePurchase from "../../Services/ServicePurchase";
import ServiceProduct from "../../Services/ServiceProduct";
import { DangerButton } from "../GeneralComponents/DangerButton";
import { PrimaryButton } from "../GeneralComponents/PrimaryButton";
import Select from "react-select";

function UpdatePurchase() {
  const [purchase, setPurchase] = useState(null);
  const [total_purchase, setTotal_Purchase] = useState(0);
  const [billNumber, setBillNumber] = useState(0);
  const [purchase_date, setPurchase_Date] = useState("");
  const [status, setStatus] = useState("");
  const [provider, setProvider] = useState("");
  const [productDetails, setProductDetails] = useState([]);
  const [quantities, setQuantities] = useState({});
  const [products, setProducts] = useState([]);
  const [highlightedProduct, setHighlightedProduct] = useState(null);
  const navigate = useNavigate();

  const { id } = useParams();
  const { ok, swalCard } = useContext(GeneralContext);

  // Función para resaltar un producto
  const highlightProduct = (productId) => {
    setHighlightedProduct(productId);
    setTimeout(() => setHighlightedProduct(null), 2000); // Quitar el resaltado después de 2 segundos
  };

  // Obtener los detalles de la compra
  const getPurchaseDetails = async (id) => {
    try {
      const response = await ServicePurchase.getPurchaseDetailsById(id);
      const purchaseDetails = response.data.data;
      setProductDetails(purchaseDetails);
      const quantities = {};
      purchaseDetails.forEach((detail) => {
        quantities[detail.product_id.id] = detail.quantity;
      });
      setQuantities(quantities);
    } catch (error) {
      console.log("Error Cargando los detalles de la compra: ", error);
    }
  };

  // Obtener todos los productos(Mientras tanto, despues se obtendra los productos filtrados por el proveedor)
  const getAllProducts = async () => {
    try {
      const response = await ServiceProduct.getAllProductsNotPaginated();
      const productsList = response.data.data;
      setProducts(productsList);
    } catch (error) {
      console.log("Error Cargando los productos: ", error);
    }
  };

  // Llenar los campos con la compra encontrada
  const getPurchaseToEdit = async (id) => {
    try {
      const response = await ServicePurchase.getPurchaseById(id);
      const purchaseFind = response.data.data;
      setPurchase(purchaseFind);
    } catch (error) {
      console.log("Error Cargando los datos: ", error);
    }
  };

  useEffect(() => {
    getAllProducts();
    getPurchaseDetails(id);
    getPurchaseToEdit(id);
  }, [id]);

  useEffect(() => {
    if (purchase) {
      setTotal_Purchase(purchase.total_purchase);
      setBillNumber(purchase.bill_number);
      setPurchase_Date(purchase.purchase_date);
      setProvider(purchase.provider_id.id);
      setStatus(purchase.state);
    }
  }, [purchase]);

  useEffect(() => {
    let subtotal = 0;
    productDetails.forEach((product) => {
      const quantity = quantities[product.product_id.id] || 0;
      const productSubtotal = quantity * product.product_id.purchase_price;
      subtotal += productSubtotal;
    });
    setTotal_Purchase(subtotal);
  }, [productDetails, quantities]);

  const handleQuantityChange = (productId, quantity) => {
    if (quantity >= 0) {
      setQuantities((prevQuantities) => ({
        ...prevQuantities,
        [productId]: quantity,
      }));
    }
  };

  const handleBillNumberChange = (billNumber) => {
    if (billNumber >= "") {
      setBillNumber(billNumber);
    }
  };

  const handleRemoveProduct = (index) => {
    const newDetails = productDetails.filter((_, i) => i !== index);
    setProductDetails(newDetails);
    const newQuantities = { ...quantities };
    delete newQuantities[productDetails[index].product_id.id];
    setQuantities(newQuantities);
  };

  const savePurchase = () => {
    const details = productDetails.map((detail) => ({
      id: detail.id || 0,
      quantity: quantities[detail.product_id.id] || 0,
      product_id: detail.product_id.id,
    }));

    const purchaseData = {
      billNumber: parseInt(billNumber),
      provider_id: parseInt(provider),
      status: status,
      details: details,
    };

    ServicePurchase.updatePurchaseWithDetails(id, purchaseData)
      .then((response) => {
        if (response.data.code === 400) {
          swalCard(
            "Error al Actualizar la Compra",
            response.data.message,
            "info"
          );
        } else if (response.data.code === 404) {
          swalCard("No se encontró", response.data.message, "info");
        } else if (response.data.code === 500) {
          swalCard(
            "Error al Actualizar la Compra",
            response.data.message,
            "error"
          );
        } else {
          ok(response.data.message, "success");
          navigate(-1);
        }
      })
      .catch((error) => {
        console.error("Error al actualizar la compra:", error);
      });
  };

  return (
    <div className="container mt-5">
      <h1 className="text-center mb-4">Actualizar la Compra</h1>
      <div className="row">
        <div className="col-md-5">
          <div className="card mb-4">
            <div className="card-header d-flex justify-content-between">
              <span>Compra</span>
              <span className="text-muted" style={{ fontSize: "0.80rem" }}>
                {purchase_date}
              </span>
            </div>
            <div className="card-body">
              <div className="mb-3">
                <label className="form-label">Total:</label>
                <span className="form-control-plaintext">
                  ${total_purchase.toLocaleString("es-CO")}
                </span>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="billNumber">
                  Numero de Factura:
                </label>
                <input
                  type="number"
                  className="form-control"
                  id="billNumber"
                  placeholder="Numero de Factura"
                  value={billNumber}
                  onChange={(e) =>
                    handleBillNumberChange(parseInt(e.target.value) || 0)
                  }
                />
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
                <DangerButton execute={() => navigate(-1)} icon={"fa-solid fa-circle-xmark"} />
                <PrimaryButton execute={savePurchase} text="Actualizar" />
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
                      <th scope="col">Precio de Compra</th>
                      <th scope="col">Cantidad</th>
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
                          {detail.product_id.purchase_price.toLocaleString("es-CO")}
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
                        {/* Validacion de no mostrar el subtotal negativo */}
                        <td>
                          $
                          {((quantities[detail.product_id.id] || 0) *
                            detail.product_id.purchase_price >
                          0
                            ? (quantities[detail.product_id.id] || 0) *
                              detail.product_id.purchase_price
                            : 0
                          ).toLocaleString("es-CO")}
                        </td>
                        <td>
                          <button
                            className="btn btn-danger"
                            onClick={() => handleRemoveProduct(index)}
                          >
                            <i class="fa-solid fa-trash-can"></i>
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

export { UpdatePurchase };
