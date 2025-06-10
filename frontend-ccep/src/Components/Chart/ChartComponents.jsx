import React, { useEffect, useRef } from "react";
import { Chart, registerables } from "chart.js";

Chart.register(...registerables);

function ChartComponents({ type, labels, data, dataLabel }) {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  useEffect(() => {
    // Obtiene el contexto del canvas
    const ctx = chartRef.current.getContext("2d");

    // Define las opciones del gráfico
    const chartOptions = {
      type: type,
      data: {
        labels: labels,
        datasets: [
          {
            label: dataLabel,
            data: data,
            fill: type === "line",
            backgroundColor:
              type === "bar"
                ? "rgba(234, 190, 63, 0.6)"
                : "rgba(234, 190, 63, 0)",
            borderColor: "rgb(236, 221, 192)",
            tension: type === "line" ? 0.1 : 0,
          },
        ],
      },
      options: {
        scales: {
          y: {
            beginAtZero: true,
            grid: {
              color: "rgba(65, 62, 56, 0.2)",
            },
          },
          x: {
            grid: {
              color: "rgba(65, 62, 56, 0.2)",
            },
          },
        },
        elements: {
          bar: {
            borderWidth: 2,
          },
          line: {
            borderWidth: 3,
          },
        },
        plugins: {
          legend: {
            labels: {
              color: "#413e38",
            },
          },
          tooltip: {
            backgroundColor: "#ffffed",
            titleColor: "#413e38",
            bodyColor: "#413e38",
          },
        },
      },
    };

    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    chartInstance.current = new Chart(ctx, chartOptions);

    return () => {
      chartInstance.current.destroy();
    };
  }, [type, labels, data]);

  return (
    <div style={{ width: "800%", height: "400px" }}>
      <canvas ref={chartRef} />
    </div>
  );
}

export { ChartComponents };
