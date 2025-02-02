import React, { useEffect, useState } from 'react';
import { useParams, Link} from 'react-router-dom';
import './stockinfo.css';

function StockInfo() {
    const { symbol } = useParams(); // Get the stock symbol from the URL
  
    const [stock, setStock] = useState(null); // Holds stock data
    const [loading, setLoading] = useState(true); // Tracks loading state
    const [error, setError] = useState(null); // Tracks errors
  
    const apiKey = "W8C3AGK5FBFV2BG3"; // Replace with your Alpha Vantage API key
  
    // Inline fetchStockDetails function inside useEffect
    useEffect(() => {
      const fetchStockDetails = async () => {
        try {
          setLoading(true);
          setError(null);
  
          const response = await fetch(
            `https://www.alphavantage.co/query?function=OVERVIEW&symbol=${symbol}&apikey=${apiKey}`
          );
  
          if (!response.ok) throw new Error("Failed to fetch stock data.");
  
          const data = await response.json();
  
          if (!data || Object.keys(data).length === 0) {
            throw new Error("Stock details not found.");
          }
  
          setStock({
            name: data.Name,
            symbol: data.Symbol,
            price: `$${parseFloat(data["52WeekHigh"]).toFixed(2)}`,
            description: data.Description,
          });
        } catch (err) {
          setError(err.message);
        } finally {
          setLoading(false);
        }
      };
  
      fetchStockDetails(); // Call the function immediately
    }, [symbol, apiKey]); // Dependencies: symbol and apiKey
  
    if (loading) return <p>Loading stock details...</p>;
    if (error) return <p>Error: {error}</p>;
  
    return (
      <div className="stock-details-container">
        <h1>{stock.name} ({stock.symbol})</h1>
        <p className="stock-price">Current Price: {stock.price}</p>
        <p className="stock-description">{stock.description}</p>
  
        <button className="back-button"> <Link to="/portfolio">Back</Link></button>
      </div>
    );
  
}

export default StockInfo;
