import { useNavigate } from 'react-router-dom';
import './portfolio.css';
import React, { useState } from 'react';
import { getPortfolioValue, addStockToWatchlist,removeStockFromWatchlist ,getUserWatchlist,getStocks,buyStock,sellStock} from '../../assets/utils/userRequests'

function Portfolio() {
  const navigate = useNavigate();
  const [stocks, setStocks] = useState([]); // Holds stock & ETF data
  const [loading, setLoading] = useState(false); // Tracks loading state
  const [error, setError] = useState(null); // Tracks errors
  const [newStockSymbol, setNewStockSymbol] = useState(""); // User input for stock/ETF symbol


  

  // Function to handle adding a stock/ETF when user clicks "Add Stock"
  const [watchlist, setWatchlist] = useState([]);
  const [stocklist, setStocklist] = useState([]);
  
  const handleAddStock = async (e) => {
    e.preventDefault();
    if (!newStockSymbol.trim()) {
      setError("Please enter a valid stock or ETF symbol.");
      return;
    }
    const username = localStorage.getItem("loggedInUsername");

    try {
      // Call addStockToWatchlist and wait for the response
      const updatedWatchlist = await addStockToWatchlist(username, newStockSymbol);
      setWatchlist(updatedWatchlist); // Update watchlist with the new list
    } catch (error) {
      setError("Failed to add stock to watchlist: " + error.message);
    }
  };

  const handleBuyStock = async (symbol, quantity, price) => {
    const username = localStorage.getItem("loggedInUsername");
    const roundedPrice = Math.round(parseFloat(price)) || 0; 
    try {
      // Call addStockToWatchlist and wait for the response
      const updatedPortfolio = await buyStock(username,quantity,symbol,roundedPrice);
      setStocklist(updatedPortfolio); // Update watchlist with the new list
    } catch (error) {
      setError("Failed to add stock to watchlist: " + error.message);
    }

  };

  const handleRemoveStockFromWatchlist = async (e) => {
    const username = localStorage.getItem("loggedInUsername");

    try {
      // Call addStockToWatchlist and wait for the response
      const updatedWatchlist = await removeStockFromWatchlist(username, e);
      setWatchlist(updatedWatchlist); // Update watchlist with the new list
    } catch (error) {
      setError("Failed to add stock to watchlist: " + error.message);
    }
  };


  const handleGetWatchlist = async (e) => {
    
    const username = localStorage.getItem("loggedInUsername");

    try {
      const updatedWatchlist = await getUserWatchlist(username);
      setWatchlist(updatedWatchlist); // Update watchlist with the new list
    } catch (error) {
      setError("Failed to add stock to watchlist: " + error.message);
    }

    try {
      const updatedPortfolio = await getStocks(username);
      console.log(updatedPortfolio);
      setStocklist(updatedPortfolio); // Update watchlist with the new list
    } catch (error) {
      setError("Failed to add stock to watchlist: " + error.message);
    }
  };

  

  const handleQuantityChange = (index, value) => {
    const updatedWatchlist = [...watchlist];
    updatedWatchlist[index].quantity = value;
    setWatchlist(updatedWatchlist);
  };
  
  const handlePriceChange = (index, value) => {
    const updatedWatchlist = [...watchlist];
    updatedWatchlist[index].price = value;
    setWatchlist(updatedWatchlist);
  };

  const handleSellStock = async (symbol, quantity) => {
    const username = localStorage.getItem("loggedInUsername");
    
    try {
      // Call addStockToWatchlist and wait for the response
      const updatedPortfolio = await sellStock(username,quantity,symbol);
      setStocklist(updatedPortfolio); // Update watchlist with the new list
    } catch (error) {
      setError("Failed to add stock to watchlist: " + error.message);
    }

  };

  return (
    <div className="portfolio-container">
      {/* Balance Section */}
      <div className="balance-section">
        <p className="balance-title">Current Portfolio Balance</p>
        <h1 className="balance-amount">$12,450.23</h1>
      </div>

      {/* Add Stock Form */}
      <div className="add-stock-container">
 

  <div className="add-stock-section">
    <form className="add-stock-form" onClick={handleAddStock}>
    <button className="add-stock-button" onClick={handleGetWatchlist}>
    Refresh
  </button>
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
</div>

      
<div className="tables-container">
  {/* WatchList Table */}
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
            <th>Actions</th>
            <th>Remove</th>
          </tr>
        </thead>
        <tbody>
          {watchlist.map((stock, index) => (
            <tr key={index}>
              <td className="stock-name">{stock.symbol}</td>
              <td className="stock-price">{stock.currentPrice}</td>
              <td className={`stock-change ${stock.isPositive ? 'positive' : 'negative'}`}>
                {stock.change}
              </td>
              <td className="stock-actions">
                <div className="actions-container">
                  <input
                    type="number"
                    onChange={(e) => handleQuantityChange(index, e.target.value)}
                    min="0"
                    className="input-field"
                    placeholder="Enter quantity"
                  />
                  <input
                    type="number"
                    onChange={(e) => handlePriceChange(index, e.target.value)}
                    min="0"
                    className="input-field"
                    placeholder="Enter price"
                  />
                  <button className="buy-button" onClick={() => handleBuyStock(stock.symbol, stock.quantity, stock.price)}>Buy</button>
                </div>
              </td>
              <td>
                <button onClick={() => handleRemoveStockFromWatchlist(stock.symbol)} className="remove-button">
                  Remove
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    )}
  </div>

  {/* Portfolio Holdings Table */}
  <div className="holding-table-section">
    <table className="holding-table">
      <thead>
        <tr>
          <th>Stock/ETF</th>
          <th>Price</th>
          <th>Amount</th>
          <th>CurrentPrice</th>
          <th>Sell</th>
        </tr>
      </thead>
    </table>
    <tbody>
    {stocklist.map((stock, index) => (
            <tr key={index}>
              <td className="stock-name">{stock.symbol}</td>
              <td className="stock-price">{stock.price}</td>
              <td className={`stock-amount `}>{stock.amount}</td>
              <td className="stock-actions">
                <div className="actions-container">
                  <input
                    type="number"
                    onChange={(e) => handleQuantityChange(index, e.target.value)}
                    min="0"
                    className="input-field"
                    placeholder="Enter quantity"
                  />
                  <input
                    type="number"
                    onChange={(e) => handlePriceChange(index, e.target.value)}
                    min="0"
                    className="input-field"
                    placeholder="Enter price"
                  />
                  <button className="sell-button" onClick={() => handleSellStock(stock.symbol, stock.quantity, stock.price)}>Sell</button>
                </div>
              </td>
              <td>
                <button onClick={() => handleRemoveStockFromWatchlist(stock.symbol)} className="remove-button">
                  Remove
                </button>
              </td>
            </tr>
          ))}
      </tbody>
  </div>
</div>
</div>
     
  );
}

export default Portfolio;
