import React, { useContext, useEffect, useState } from "react";
import Select from "react-select";
import ServiceSupplier from "../Services/ServiceSupplier";
import ServiceProduct from "../Services/ServiceProduct";
import ServicePurchase from "../Services/ServicePurchase";
import { PrimaryButton } from "../Components/GeneralComponents/PrimaryButton";
import { DangerButton } from "../Components/GeneralComponents/DangerButton";
import { GeneralContext } from "../Context/GeneralContext";

const EdcSystem = () => {
  const [suppliers, setSuppliers] = useState([]);
  const [selectedSupplier, setSelectedSupplier] = useState();
  const [products, setProducts] = useState([]);
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [disableSupplierSelect, setDisableSupplierSelect] = useState(false);
  const [total, setTotal] = useState(0);
  const [billNumber, setBillNumber] = useState("");

  // Texto para mostrar errores de validacion
  const [billNumberError, setBillNumberError] = useState();

  const { ok, swalCard } = useContext(GeneralContext);

  // Llamado al service para listar proveedores
  const suppliersList = () => {
    ServiceSupplier.getAllSuppliersNotPaginated()
      .then((response) => {
        setSuppliers(response.data.data);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  // Método para buscar productos por proveedor
  const findProductByProvider = (providerId) => {
    ServiceProduct.findProductByProvider(providerId)
      .then((response) => {
        setProducts(response.data.data);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    suppliersList();
  }, []);

  useEffect(() => {
    // Calcula el total cuando cambian los productos seleccionados
    let newTotal = selectedProducts.reduce(
      (acc, product) => acc + product.subtotal,
      0
    );
    setTotal(newTotal);
  }, [selectedProducts]);

  const handleSupplierChange = (selectedOption) => {
    const selectedSupplier = suppliers.find(
      (supplier) => supplier.id === selectedOption.value
    );
    setSelectedSupplier(selectedSupplier);
    setDisableSupplierSelect(true); // Deshabilitar el select de proveedores
    findProductByProvider(selectedSupplier.id); // Obtener productos por proveedor
  };

  const handleProductSelect = (selectedOption) => {
    const selectedProduct = products.find(
      (product) => product.id === selectedOption.value
    );
  
    // Verificar si el producto ya está en la lista de productos seleccionados
    const productExists = selectedProducts.some(
      (product) => product.id === selectedProduct.id
    );
  
    if (!productExists) {
      setSelectedProducts([
        ...selectedProducts,
        {
          ...selectedProduct,
          quantity: 1,
          subtotal: selectedProduct.purchase_price,
        },
      ]);
    } else {
      // Aquí podrías mostrar un mensaje de que el producto ya está en la lista si lo deseas
      console.log("El producto ya está en la lista");
    }
  };
  

  const handleQuantityChange = (index, quantity) => {
    const newSelectedProducts = [...selectedProducts];
    newSelectedProducts[index].quantity = quantity;
    newSelectedProducts[index].subtotal =
      quantity * newSelectedProducts[index].purchase_price;
    setSelectedProducts(newSelectedProducts);
  };

  const handleRemoveProduct = (index) => {
    const newSelectedProducts = selectedProducts.filter((_, i) => i !== index);
    setSelectedProducts(newSelectedProducts);
  };

  const handleSavePurchase = () => {
    if (billNumber.toString().trim() === "") {
      setBillNumberError("Este campo es requerido");
      return;
    } else if (billNumber.length > 10) {
      setBillNumberError(
        "El numero de factura no puede tener mas de 10 digitos"
      );
      return;
    } else {
      setBillNumberError("");
    }

    // Construir objeto de compra
    const purchase = {
      provider_id: selectedSupplier.id,
      billNumber: parseInt(billNumber),
      total: total,
      details: selectedProducts.map((product) => ({
        product_id: product.id,
        quantity: product.quantity,
        subtotal: product.subtotal,
      })),
    };

    // Guardar compra
    ServicePurchase.savePurchaseWithDetails(purchase)
      .then((response) => {
        if (response.data.code === 400) {
          swalCard(
            "Error al registrar la compra",
            response.data.message,
            "info"
          );
        } else if (response.data.code === 404) {
          swalCard("No se encontró", response.data.message, "info");
        } else if (response.data.code === 500) {
          swalCard(
            "Error al registrar la compra",
            response.data.message,
            "error"
          );
        } else {
          ok(response.data.message, "success");
          // Limpiar estado después de guardar la compra
          setSelectedSupplier(null);
          setSelectedProducts([]);
          setTotal(0);
          setBillNumber("");
          setDisableSupplierSelect(false);
        }
      })
      .catch((error) => {
        console.error("Error al guardar la compra", error);
      });
  };

  return (
    <div className="container mt-5">
      <h1 className="text-center mb-4">Registrar Compras</h1>
      <Select
        options={suppliers.map((supplier) => ({
          label: supplier.name,
          value: supplier.id,
        }))}
        onChange={handleSupplierChange}
        placeholder="Buscar y añadir proveedor..."
        isDisabled={disableSupplierSelect}
      />
      {selectedSupplier && (
        <div className="card mt-4">
          <div className="card-body">
            <h5 className="card-title">
              Productos del Proveedor: {selectedSupplier.name}
            </h5>
            <Select
              options={products.map((product) => ({
                label: product.name,
                value: product.id,
              }))}
              onChange={handleProductSelect}
              placeholder="Seleccionar producto..."
            />
            {selectedProducts.length > 0 && (
              <div className="mt-4">
                <div className="scrollable-summary">
                  <div className="table-responsive">
                    <table className="table table-bordered caption-top align-items-center">
                      <thead>
                        <tr>
                          <th>Nombre del producto</th>
                          <th>Cantidad</th>
                          <th>Precio de Compra</th>
                          <th>Subtotal</th>
                          <th>Acciones</th>
                        </tr>
                      </thead>
                      <tbody>
                        {selectedProducts.map((product, index) => (
                          <tr key={product.id}>
                            <td>{product.name}</td>
                            <td className="text-center">
                              <div className="d-flex justify-content-center">
                                <input
                                  type="number"
                                  min="1"
                                  value={product.quantity}
                                  onChange={(e) =>
                                    handleQuantityChange(
                                      index,
                                      parseInt(e.target.value, 10)
                                    )
                                  }
                                  className="form-control w-50"
                                />
                              </div>
                            </td>
                            <td>$ {product.purchase_price.toLocaleString("es-CO")}</td>
                            <td className="text-center">
                              $ {product.subtotal.toLocaleString("es-CO")}
                            </td>
                            <td className="text-center">
                              <DangerButton
                                execute={() => handleRemoveProduct(index)}
                                icon={"fa-solid fa-trash-can"}
                              />
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}
          </div>
          <div class="list-group list-group-flush">
            <li class="list-group-item">
              <div className="form-group">
                <label htmlFor="billNumber">Numero de Factura:</label>
                <input
                  id="billNumber"
                  name="billNumber"
                  type="number"
                  value={billNumber}
                  className={`form-control ${billNumberError && "error"}`}
                  onChange={(e) => setBillNumber(e.target.value)}
                  placeholder="Numero de Factura"
                />
                {billNumberError && <div className="text-danger">{billNumberError}</div>}
              </div>
              <br />
              <h5>Total: ${total.toLocaleString("es-CO")}</h5>
              <PrimaryButton
                text={"Finalizar"}
                icon={"fa-solid fa-check"}
                execute={handleSavePurchase}
              />
            </li>
          </div>
        </div>
      )}
    </div>
  );
};

export { EdcSystem };
