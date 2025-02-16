import { useNavigate } from 'react-router-dom';
import './portfolio.css';
import React, { useState } from 'react';
import { getPortfolioValue, addStockToWatchlist,removeStockFromWatchlist ,getUserWatchlist,getStocks,buyStock,sellStock,getProfitOnStock} from '../../assets/utils/userRequests'
import DeleteIcon from '@mui/icons-material/Delete';
import { Box, TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody, Stack, Divider, TextField, Button } from "@mui/material";
import { map } from 'framer-motion/client';


function Portfolio() {
  const navigate = useNavigate();
  const [stocks, setStocks] = useState([]); // Holds stock & ETF data
  const [loading, setLoading] = useState(false); // Tracks loading state
  const [error, setError] = useState(null); // Tracks errors
  const [newStockSymbol, setNewStockSymbol] = useState(""); // User input for stock/ETF symbol
  const [watchlist, setWatchlist] = useState([]);
  const [stocklist, setStocklist] = useState([]);
  const [profitlist, setProfit] = useState([]);
  const [sellList, setSellList] = useState([]);
  

  
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

      const updatedUser = await buyStock(username,quantity,symbol,roundedPrice);
      const updatedPortfolio = updatedUser["stocks"];
      console.log(updatedPortfolio);
      setStocklist(updatedPortfolio); 
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


  const handleGetWatchlist = async () => {
    const username = localStorage.getItem("loggedInUsername");
  
    try {
      const updatedWatchlist = await getUserWatchlist(username);
      setWatchlist(updatedWatchlist); 
    } catch (error) {
      setError("Failed to fetch watchlist: " + error.message);
    }
  
    try {
      const updatedPortfolio = await getStocks(username);
  

      const updatedPortfolioWithProfit = await Promise.all(
        updatedPortfolio.map(async (stock) => {
          const profit = await getProfitOnStock(username, stock.symbol);
          return { ...stock, profit };
        })
      );
  
      localStorage.setItem("stocks", JSON.stringify(updatedPortfolioWithProfit));
      console.log(updatedPortfolioWithProfit);
      setStocklist(updatedPortfolioWithProfit);
    } catch (error) {
      setError("Failed to fetch stocks: " + error.message);
    }
  };

  const handleSellQuantityChange = (index, value) => {
    const updatedSellList = [...sellList];
    updatedSellList[index] = value;
    setSellList(updatedSellList);
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
      await sellStock(username, quantity, symbol); // Execute sell operation
  
      const updatedPortfolio = await getStocks(username); // Fetch updated portfolio
  
      // Ensure profit calculations are completed before updating state
      const updatedPortfolioWithProfit = await Promise.all(
        updatedPortfolio.map(async (stock) => {
          const profit = await getProfitOnStock(username, stock.symbol);
          return { ...stock, profit }; // Create a new object with the profit value
        })
      );
  
      setStocklist(updatedPortfolioWithProfit); // Update stocklist after profit is added
    } catch (error) {
      setError("Failed to sell stock: " + error.message);
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
        <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              minHeight: "100vh",
              width: "100vw",
              overflow: "hidden",
              padding: 4, 
              boxSizing: "border-box",
            }}
        >
          <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={2}>
          
          <TableContainer component={Paper} className="watchlist-table">
            <Table sx={{ minWidth: 900 }} style={{}} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell>Stock Symbol</TableCell>
                  <TableCell align="right">Change</TableCell>
                  <TableCell align="right">Change (%)</TableCell>
                  <TableCell align="right">Current Price</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {watchlist ? (
                  watchlist.map((stock, index) => (
                    <TableRow key={stock.symbol} sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
                      <TableCell component="th" scope="row">
                        {stock.symbol}
                      </TableCell>
                      <TableCell align="right">{stock.change}</TableCell>
                      <TableCell align="right">{stock.changePercent}</TableCell>
                      <TableCell align="right">{stock.currentPrice}</TableCell>
                      <TableCell align="right">
                        <Stack direction="row" divider={<Divider orientation="vertical" flexItem />} spacing={2}>
                          <TextField
                            id="standard-number"
                            label="Quantity"
                            type="number"
                            variant="standard"
                            slotProps={{
                              inputLabel: {
                                shrink: true,
                              },
                            }}
                            onChange={(e) => handleQuantityChange(index, e.target.value)}
                          />
                          <TextField
                            id="standard-number"
                            label="Price"
                            type="number"
                            variant="standard"
                            slotProps={{
                              inputLabel: {
                                shrink: true,
                              },
                            }}
                            onChange={(e) => handlePriceChange(index, e.target.value)}
                          />
                          <Button variant="outlined" color="success" onClick={() => {
                            handleBuyStock(stock.symbol, stock.quantity, stock.price);
                            console.log("Buy button clicked");
                          }}>Buy</Button>
                        </Stack>
                      </TableCell>
                      <TableCell align="right"><
                        Button variant="outlined" startIcon={<DeleteIcon />} color="error" onClick={() => {
                          handleRemoveStockFromWatchlist(stock.symbol);
                          console.log("Remove button clicked");
                        }}>Remove</Button>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={4} align="center">
                      {error ? error : "No stocks in watchlist"}
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>

          <TableContainer component={Paper} className="holdings-table">
            <Table sx={{ minWidth: 900 }} style={{}} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell>Stock Symbol</TableCell>
                  <TableCell align="right">Price</TableCell>
                  <TableCell align="right">Quantiy</TableCell>
                  <TableCell align="right">Open/Gain Loss</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {stocklist.length > 0 ? (
                  stocklist.map((stock, index) => (
                    <TableRow key={stock.symbol} sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
                      <TableCell component="th" scope="row">
                        {stock.symbol}
                      </TableCell>
                      <TableCell align="right">{stock.price}</TableCell>
                      <TableCell align="right">{stock.amount}</TableCell>
                      <TableCell align="right">{stock.profit}</TableCell>
                      <TableCell align="right">
                        <Stack direction="row" divider={<Divider orientation="vertical" flexItem />} spacing={2}>
                          <TextField
                            id="standard-number"
                            label="Quantity"
                            type="number"
                            variant="standard"
                            slotProps={{
                              inputLabel: {
                                shrink: true,
                              },
                            }}
                            onChange={(e) => handleSellQuantityChange(index, e.target.value)}
                          />
                          <Button variant="outlined" color="success" onClick={() => {
                            handleSellStock(stock.symbol, sellList[index]);
                            console.log("Buy button clicked");
                          }}>Sell</Button>
                        </Stack>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={4} align="center">
                      {error ? error : "No stocks in watchlist"}
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>

          
          
          </Stack>
          

        </Box>

  



</div>
</div>
     
  );
}

export default Portfolio;
