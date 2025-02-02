import { useNavigate } from 'react-router-dom';
import './portfolio.css';
import React, { useState, useEffect } from 'react';

function Portfolio() {
  const navigate = useNavigate();

  const [stocks, setStocks] = useState([]); // Holds stock data
  const [loading, setLoading] = useState(false); // Tracks loading state
  const [error, setError] = useState(null); // Tracks errors
  const [newStockSymbol, setNewStockSymbol] = useState(""); // Tracks user input for new stock

  const apiKey = "W8C3AGK5FBFV2BG3"; // Replace with your Alpha Vantage API key

  // Function to fetch a single stock by symbol
  const fetchStockBySymbol = async (symbol) => {
    try {
      setLoading(true);
      setError(null); // Clear previous errors

      const response = await fetch(
        `https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${symbol}&apikey=${apiKey}`
      );

      if (!response.ok) throw new Error(`Failed to fetch stock data for ${symbol}`);

      const data = await response.json();
      const stockData = data["Global Quote"];

      if (!stockData || !stockData["05. price"]) {
        throw new Error(`Stock symbol "${symbol}" not found.`);
      }

      const newStock = {
        name: stockData["01. symbol"],
        price: `$${parseFloat(stockData["05. price"]).toFixed(2)}`,
        change: `${parseFloat(stockData["10. change percent"]).toFixed(2)}%`,
        isPositive: parseFloat(stockData["09. change"]) >= 0,
      };

      setStocks((prevStocks) => [...prevStocks, newStock]); // Add new stock to list
      setNewStockSymbol(""); // Clear input field
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Function to handle adding a new stock
  const handleAddStock = (e) => {
    e.preventDefault();
    if (!newStockSymbol.trim()) {
      setError("Please enter a valid stock symbol.");
      return;
    }
    fetchStockBySymbol(newStockSymbol.toUpperCase());
  };

  return (
    <div className="portfolio-container">
      {/* Balance Section */}
      <div className="balance-section">
        <p className="balance-title">Current Portfolio Balance</p>
        <h1 className="balance-amount">$12,450.23</h1>
      </div>

      {/* Add Stock Form */}
      <form className="add-stock-form" onSubmit={handleAddStock}>
        <input
          type="text"
          placeholder="Enter Stock Symbol (e.g., AAPL)"
          value={newStockSymbol}
          onChange={(e) => setNewStockSymbol(e.target.value)}
        />
        <button type="submit" className="add-stock-button">Add Stock</button>
      </form>

      {/* Show error messages */}
      {error && <p className="error-message">{error}</p>}

      {/* Stock Holdings Table */}
      {loading ? (
        <p>Loading stocks...</p>
      ) : (
        <table className="stock-table">
          <thead>
            <tr>
              <th>Stock</th>
              <th>Price</th>
              <th>Change</th>
            </tr>
          </thead>
          <tbody>
            {stocks.map((stock, index) => (
              <tr key={index}>
                <td className="stock-name">{stock.name}</td>
                <td className="stock-price">{stock.price}</td>
                <td className={`stock-change ${stock.isPositive ? 'positive' : 'negative'}`}>
                  {stock.change}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Back to Home Button */}
      <button className="back-button" onClick={() => navigate('/')}>
        Back to Home
      </button>
    </div>
  );
}

export default Portfolio;
