import { useNavigate } from 'react-router-dom';
import './portfolio.css';
import React, { useState, useEffect } from 'react';

function Portfolio() {
  const navigate = useNavigate();

  const [stocks, setStocks] = useState([]); // Holds stock data
  const [loading, setLoading] = useState(true); // Tracks loading state
  const [error, setError] = useState(null); // Tracks errors

  useEffect(() => {
    fetchStocks(); // Fetch stocks when the component loads
  }, []);

  const fetchStocks = async () => {
    try {
      const stockSymbols = ["AAPL", "TSLA", "AMZN", "MSFT"]; // Stocks to fetch
      const apiKey = "W8C3AGK5FBFV2BG3"; // Replace with your Alpha Vantage API key
      const fetchedStocks = [];

      for (const symbol of stockSymbols) {
        const response = await fetch(
          `https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${symbol}&apikey=${apiKey}`
        );

        if (!response.ok) throw new Error(`Failed to fetch stock data for ${symbol}`);

        const data = await response.json();
        const stockData = data["Global Quote"];

        if (stockData) {
          fetchedStocks.push({
            name: `${symbol}`, // Stock symbol
            price: `$${parseFloat(stockData["05. price"]).toFixed(2)}`, // Current stock price
            change: `${parseFloat(stockData["10. change percent"]).toFixed(2)}%`, // Change percentage
            isPositive: parseFloat(stockData["09. change"]) >= 0, // Positive or negative change
          });
        }
      }

      setStocks(fetchedStocks); // Update the stocks state
    } catch (err) {
      setError(err.message); // Set error if fetching fails
    } finally {
      setLoading(false); // Stop loading spinner
    }
  };

  return (
    <div className="portfolio-container">
      {/* Balance Section */}
      <div className="balance-section">
        <p className="balance-title">Current Portfolio Balance</p>
        <h1 className="balance-amount">$12,450.23</h1>
      </div>

      {/* Stock Holdings Table */}
      {loading ? (
        <p>Loading stocks...</p>
      ) : error ? (
        <p className="error-message">{error}</p>
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
