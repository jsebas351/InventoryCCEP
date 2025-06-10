import React, { useEffect, useState } from "react";
import "../Styles/StartPage/StartPage.css";
import { ChartComponents } from "../Components/Chart/ChartComponents";
import reportServiceInstance from "../Services/ServiceReport";

function StartPage() {
  const [labelsCountSales, setLabelsCountSales] = useState([]);
  const [dataCountSales, setDataCountSales] = useState([]);
  const [dataTotalRevenue, setDataTotalRevenue] = useState([]);
  const [todayCountSales, setTodayCountSales] = useState(0); 
  const [todayTotalRevenue, setTodayTotalRevenue] = useState(0);

  useEffect(() => {
    const fetchSalesSummary = async () => {
      try {
        // Obtener la fecha actual y las fechas de los últimos 5 días
        const today = new Date();
        const dates = [];
        for (let i = 0; i < 5; i++) {
          const date = new Date(today);
          date.setDate(today.getDate() - i);
          dates.push(date);
        }

        // Convertir las fechas a un formato 'yyyy-mm-dd'
        const dateLabels = dates
          .map((date) => {
            const d = new Date(date);
            return d.toISOString().split('T')[0]; // Formato yyyy-mm-dd
          })
          .reverse();

        const startDate = dates[4].toISOString().split("T")[0];
        const endDate = dates[0].toISOString().split("T")[0];

        // Llamar al servicio para obtener el resumen de ventas
        const response = await reportServiceInstance.getSalesSummary(
          startDate,
          endDate
        );

        if (response.data.success) {
          const salesData = response.data.data;
          const salesByDate = {};
          const totalRevenueByDate = {};

          // Agrupar las ventas y los ingresos por fecha
          salesData.forEach((item) => {
            const saleDate = new Date(item.saleDate).toISOString().split('T')[0]; // Formato yyyy-mm-dd
            salesByDate[saleDate] = item.totalSales;
            totalRevenueByDate[saleDate] = item.totalRevenue;
          });

          // Llenar los datos de ventas y labelsCountSales
          const salesValues = dateLabels.map(
            (label) => salesByDate[label] || 0
          );

          const totalRevenueValues = dateLabels.map(
            (label) => totalRevenueByDate[label] || 0
          );

          // Obtener el conteo de ventas del día actual
          const todayFormatted = new Date().toISOString().split('T')[0];
          setTodayCountSales(salesByDate[todayFormatted] || 0); 
          setTodayTotalRevenue(totalRevenueByDate[todayFormatted] || 0); 

          setLabelsCountSales(dateLabels);
          setDataCountSales(salesValues);
          setDataTotalRevenue(totalRevenueValues);
        }
      } catch (error) {
        console.error("Error al obtener el resumen de ventas:", error);
      }
    };

    fetchSalesSummary();
  }, []);

  return (
    <div>
      <h2>Inicio</h2>
      <div className="card-container-start">
        <div className="card-start">Cantidad Ventas: {todayCountSales}</div>
        <div className="card-start">Valor Total Ventas: ${todayTotalRevenue.toLocaleString()}</div>
      </div>
      <div className="additional-content row">
        <h3 className="text-center">Reporte de ventas en los días</h3>
        <br />
        <div className="col-md-6">
          <div>
            <ChartComponents
              type={"line"}
              dataLabel={"Cantidad Ventas"}
              labels={labelsCountSales}
              data={dataCountSales}
            />
          </div>
        </div>
        <div className="col-md-6">
          <div>
            <ChartComponents
              type={"line"}
              dataLabel={"Ingresos"}
              labels={labelsCountSales}
              data={dataTotalRevenue}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export { StartPage };
