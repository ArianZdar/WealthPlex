import React from 'react';
import { useNavigate } from 'react-router-dom';
import './portfolio.css';

function Portfolio() {
  const navigate = useNavigate();

  // Example portfolio data
  const balance = "$12,450.23"; // Your current money
  const stocks = [
    { name: "Apple (AAPL)", price: "$174.55", change: "+2.4%", isPositive: true },
    { name: "Tesla (TSLA)", price: "$715.67", change: "-1.8%", isPositive: false },
    { name: "Amazon (AMZN)", price: "$3,145.22", change: "+0.9%", isPositive: true },
    { name: "Microsoft (MSFT)", price: "$289.12", change: "+1.2%", isPositive: true },
  ];

  return (
    <div className="portfolio-container">
      {/* Balance Section */}
      <div className="balance-section">
        <p className="balance-title">Current Portfolio Balance</p>
        <h1 className="balance-amount">{balance}</h1>
      </div>

      {/* Stock Holdings Table */}
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

      {/* Back to Home Button */}
      <button className="back-button" onClick={() => navigate('/')}>
        Back to Home
      </button>
    </div>
  );
}

export default Portfolio;
