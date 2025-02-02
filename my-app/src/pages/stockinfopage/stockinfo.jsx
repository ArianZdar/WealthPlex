import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import './stockinfo.css';

function StockInfo() {
  const { symbol } = useParams(); // Get the stock ticker from the URL
  const [stock, setStock] = useState(null); // Holds stock data
  const [loading, setLoading] = useState(true); // Tracks loading state
  const [error, setError] = useState(null); // Tracks errors

  useEffect(() => {
    const fetchStockDetails = async () => {
      try {
        setLoading(true);
        setError(null);

        // Replace with your backend API endpoint
        const response = await fetch(`/api/stocks/${symbol}`);
        if (!response.ok) throw new Error('Failed to fetch stock data.');

        const data = await response.json();

        if (!data || Object.keys(data).length === 0) {
          throw new Error('Stock details not found.');
        }

        // Set stock data
        setStock({
          ticker: data.ticker, // E.g., 'AAPL'
          number: data.number, // Dynamic number (e.g., price, volume, etc.)
          description: data.description, // Stock description from backend
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchStockDetails();
  }, [symbol]);

  if (loading) return <p>Loading stock details...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div className="stock-details-container">
      <h1 className="stock-ticker">Ticker: {stock.ticker}</h1>
      <p className="stock-number">Dynamic Number: {stock.number}</p>
      <p className="stock-description">{stock.description}</p>
      <button className="back-button">
        <Link to="/portfolio" className="back-link">Back to Portfolio</Link>
      </button>
    </div>
  );
}

export default StockInfo;
