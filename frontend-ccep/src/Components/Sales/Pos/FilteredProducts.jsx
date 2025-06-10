import React, { useEffect, useState, useRef } from "react";
import { PrimaryButton } from "../../GeneralComponents/PrimaryButton";
import ServiceProduct from "../../../Services/ServiceProduct";

function FilteredProducts({ onAddProduct, selectedProducts }) {
  const [products, setProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(false);
  const inputRef = useRef(null);

  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleAddProduct = (product) => {
    onAddProduct(product);
  };

  useEffect(() => {
    const searchProduct = async () => {
      setLoading(true);
      try {
        const response = await ServiceProduct.filteredProducts({
          name: searchTerm,
        });

        // Verificar si response.data es null antes de acceder a response.data.data
        const filteredProducts =
          response.data && response.data.data
            ? response.data.data.filter(
                (product) =>
                  !selectedProducts.some(
                    (selectedProduct) => selectedProduct.id === product.id
                  )
              )
            : [];

        setProducts(filteredProducts);
      } catch (error) {
        console.error("Error fetching products:", error);
      } finally {
        setLoading(false);
      }
    };

    searchProduct();

    inputRef.current.focus();
  }, [searchTerm, selectedProducts]); // Añade selectedProducts como dependencia

  return (
    <>
      <h2>Filtrar Productos Por Nombre</h2>
      <br />
      <div className="container text-center">
        <div className="row mb-3">
          <div className="col">
            <input
              ref={inputRef}
              type="text"
              className="form-control"
              placeholder="Buscar producto por referencia o nombre..."
              value={searchTerm}
              onChange={handleSearchChange}
            />
          </div>
        </div>
        <hr />
        {loading ? (
          <i className="fa-solid fa-arrow-rotate-right fa-spin"></i>
        ) : products === null || products.length === 0 ? (
          <div className="alert alert-warning" role="alert">
            No se encontró el Producto
          </div>
        ) : (
          <div className="scrollable-products">
            <div className="row row-cols-3">
              {products.map((product) => {
                return (
                  <div className="col p-3" key={product.id}>
                    <div
                      className="card"
                      style={{ width: "16rem", height: "100%" }}
                    >
                      <div className="card-body">
                        <h5 className="card-title" style={{ color: "#eabe3f" }}>
                          {product.name}
                        </h5>
                        <hr />
                        <h6 className="card-subtitle mb-2 text-muted">
                          Referencia: {product.reference}
                        </h6>
                        <hr />
                        <h6 className="card-subtitle mb-2">
                          Precio: ${product.sale_price.toLocaleString("es-CO")}
                        </h6>
                        <hr />
                        <p className="card-text">{product.description}</p>
                        <PrimaryButton
                          text={"Agregar"}
                          icon={"fa-solid fa-cart-plus"}
                          execute={() => handleAddProduct(product)}
                        />
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>
    </>
  );
}

export { FilteredProducts };
