import { useNavigate } from 'react-router-dom';
import './portfolio.css';
import React, { useState } from 'react';

function Portfolio() {
  const navigate = useNavigate();
  const [stocks, setStocks] = useState([]); // Holds stock & ETF data
  const [loading, setLoading] = useState(false); // Tracks loading state
  const [error, setError] = useState(null); // Tracks errors
  const [newStockSymbol, setNewStockSymbol] = useState(""); // User input for stock/ETF symbol

  const apiKey = "W8C3AGK5FBFV2BG3"; // Replace with your Alpha Vantage API key

  // Function to fetch a stock/ETF by symbol
  const fetchStockBySymbol = async (symbol) => {
    try {
      setLoading(true);
      setError(null);

      // Step 1: Use SYMBOL_SEARCH to find the correct asset
      const searchResponse = await fetch(
        `https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=${symbol}&apikey=${apiKey}`
      );

      const searchData = await searchResponse.json();

      if (!searchData.bestMatches || searchData.bestMatches.length === 0) {
        throw new Error(`Symbol "${symbol}" not found.`);
      }

      // Extract the best match from the search results
      const bestMatch = searchData.bestMatches[0]["1. symbol"];

      // Step 2: Fetch real-time price data using the matched symbol
      const quoteResponse = await fetch(
        `https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${bestMatch}&apikey=${apiKey}`
      );

      const quoteData = await quoteResponse.json();
      const stockData = quoteData["Global Quote"];

      if (!stockData || !stockData["05. price"]) {
        throw new Error(`Could not retrieve data for "${bestMatch}".`);
      }

      // Step 3: Format the new stock/ETF entry
      const newStock = {
        name: bestMatch,
        price: `$${parseFloat(stockData["05. price"]).toFixed(2)}`,
        change: `${parseFloat(stockData["10. change percent"]).toFixed(2)}%`,
        isPositive: parseFloat(stockData["09. change"]) >= 0,
      };

      // Step 4: Add to state
      setStocks((prevStocks) => [...prevStocks, newStock]);
      setNewStockSymbol(""); // Clear input field
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Function to handle adding a stock/ETF when user clicks "Add Stock"
  const handleAddStock = (e) => {
    e.preventDefault();
    if (!newStockSymbol.trim()) {
      setError("Please enter a valid stock or ETF symbol.");
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
      <div className="add-stock-section">
        <form className="add-stock-form" onSubmit={handleAddStock}>
          <input
            type="text"
            placeholder="Enter Stock/ETF Symbol (e.g., AAPL, VFV)"
            value={newStockSymbol}
            onChange={(e) => setNewStockSymbol(e.target.value)}
          />
          <button type="submit" className="add-stock-button">Add Stock</button>
        </form>
        {error && <p className="error-message">{error}</p>}
      </div>

      {/* Stock Holdings Table */}
      <div className="stock-table-section">
        {loading ? (
          <p>Loading stocks...</p>
        ) : (
          <table className="stock-table">
            <thead>
              <tr>
                <th>Stock/ETF</th>
                <th>Price</th>
                <th>Change</th>
              </tr>
            </thead>
            <tbody>
              {stocks.map((stock, index) => (
                <tr key={index}>
                  <td className="stock-name">{stock.name}</td>
                  <td className="stock-price">{stock.price}</td>
                  <td
                    className={`stock-change ${
                      stock.isPositive ? 'positive' : 'negative'
                    }`}
                  >
                    {stock.change}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default Portfolio;
