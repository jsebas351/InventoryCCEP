import React, { useEffect, useState } from 'react';
import ServiceInventory from "../Services/ServiceInventory";
import { Pagination } from '../Components/GeneralComponents/Pagination';

function Inventories () {

  const [inventories, setInventories] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);

  // Llamado al service para listar el inventario
  const getInventories = (page, size) => {
    ServiceInventory.getInventory(page, size)
    .then((response) => {
      setInventories(response.data.data.content);
      setTotalPages(response.data.data.totalPages);
    }).catch((error) => {
      console.log(error);
    })
  }

  //Actualizar el estado de la pagina = Page
  const handlePageChange = (pageNumber) => {
    setPage(pageNumber - 1);
  };

  //Listar categorias cada vez que haya un cambio en las paginas y el tamaño
  useEffect(() => {
    getInventories(page, size);
  }, [page, size]);

  return (
    <div>
      <div className="container">
        <br />
        <h2 className="text-center">Inventario</h2>
        <div className="table-responsive">
          <table className="table caption-top table-bordered">
            <caption>Inventario</caption>
            <thead>
              <tr>
              <th scope="col" style={{ width: '60%' }}>Producto</th>
              <th scope="col" style={{ width: '40%' }}>Stock</th>
              </tr>
            </thead>
            <tbody>
              {inventories.map((inventory) => (
                <tr key={inventory.id}>
                  <td>{inventory.product_id.name}</td>
                  <td>{inventory.stock}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <Pagination
            totalPages={totalPages}
            currentPage={page + 1}
            onPageChange={handlePageChange}
          />
        </div>
      </div>
    </div>
  )
}

export { Inventories };