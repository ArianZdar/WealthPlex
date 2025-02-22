// import React, { useEffect, useState } from 'react';
// import { useParams, Link } from 'react-router-dom';
// import './stockinfo.css';
// import { 
//   LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer 
// } from 'recharts';


// function StockInfo() {
//   const { symbol } = useParams(); // Get the stock ticker from the URL
//   const [stock, setStock] = useState(null); // Holds stock data
//   const [loading, setLoading] = useState(true); // Tracks loading state
//   const [error, setError] = useState(null); // Tracks errors
//   const [chartData, setChartData] = useState([]); // Holds historical stock prices

//   useEffect(() => {
//     const fetchStockDetails = async () => {
//       try {
//         setLoading(true);
//         setError(null);

//         // Replace with your backend API endpoint
//         const response = await fetch(`/api/stocks/${symbol}`);
//         if (!response.ok) throw new Error('Failed to fetch stock data.');

//         const data = await response.json();

//         if (!data || Object.keys(data).length === 0) {
//           throw new Error('Stock details not found.');
//         }

//         // Set stock data
//         setStock({
//           ticker: data.symbol, 
//           currentPrice: data.currentPrice,
//           highPrice: data.highPrice,
//           lowPrice: data.lowPrice,
//           volume: data.volume,
//           changePercent: data.changePercent,
//         });
//         fetchStockGraphData(symbol);

//       } catch (err) {
//         setError(err.message);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchStockDetails();
//   }, [symbol]);

//   const fetchStockGraphData = async (symbol) => {
//     try {

//       const response = await fetch(`/api/stocks/history/${symbol}`);
//       if (!response.ok) throw new Error('Failed to fetch stock graph data.');

//       const data = await response.json();
//       if (!data || !data["Time Series (Daily)"]) {
//         throw new Error("Invalid data format from API");
//       }

//       const formattedData = Object.entries(data["Time Series (Daily)"])
//         .map(([date, values]) => ({
//           date,
//           close: parseFloat(values["4. close"]),
//         }))
//         .reverse();

//       setChartData(formattedData);
//     } catch (err) {
//       console.error("Error fetching stock graph data:", err);
//     }
//   };

//   if (loading) return <p>Loading stock details...</p>;
//   if (error) return <p>Error: {error}</p>;

//   return (
//     <div className="stock-details-container">
//       <h1 className="stock-ticker">Ticker: {stock.ticker}</h1>
//       <p className="stock-price">Current Price: ${stock.currentPrice}</p>
//       <p className="stock-change">Change: {stock.changePercent}</p>
//       <p className="stock-volume">Volume: {stock.volume}</p>

//       {/*Stock Price Chart */}
//       <h2>Stock Price History</h2>
//       <ResponsiveContainer width="100%" height={300}>
//         <LineChart data={chartData}>
//           <CartesianGrid strokeDasharray="3 3" />
//           <XAxis dataKey="date" tickFormatter={(tick) => tick.slice(5)} />
//           <YAxis domain={['auto', 'auto']} />
//           <Tooltip />
//           <Line type="monotone" dataKey="close" stroke="#82ca9d" strokeWidth={2} />
//         </LineChart>
//       </ResponsiveContainer>

//       <button className="back-button">
//         <Link to="/portfolio" className="back-link">Back to Portfolio</Link>
//       </button>
//     </div>
//   );
// }

// export default StockInfo;
