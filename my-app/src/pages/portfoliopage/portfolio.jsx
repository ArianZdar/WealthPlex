import { useNavigate } from 'react-router-dom';
import './portfolio.css';
import React, { useState,useEffect } from 'react';
import { getPortfolioValue,setLongTermInvestor, addStockToWatchlist, removeStockFromWatchlist, getUserWatchlist, getStocks, buyStock, sellStock, getProfitOnStock, getUserProfit } from '../../assets/utils/userRequests';
import { getStockRequests,getStockRating,getStockExplanation ,getStockHistory} from '../../assets/utils/stockInfoRequests';
import DeleteIcon from '@mui/icons-material/Delete';
import { Card, CardContent, Switch,FormControlLabel, Box, TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody, Stack, Divider, TextField, Button, Autocomplete, ButtonGroup, Typography, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";
import { LineChart ,Gauge } from '@mui/x-charts/';

function Portfolio() {
  const navigate = useNavigate();
  const [stocks, setStocks] = useState([]); 
  const [loading, setLoading] = useState(false); 
  const [error, setError] = useState(null); 
  const [newStockSymbol, setNewStockSymbol] = useState(""); 
  const [watchlist, setWatchlist] = useState([]); 
  const [stocklist, setStocklist] = useState([]); 
  const [profitlist, setProfit] = useState([]);
  const [sellList, setSellList] = useState([]);
  const [priceHistory, setPriceHistory] = useState([]);
  const [dateList, setDateList] = useState([]);
  const [stockRecommendations, setStockRecommendations] = useState([]);
  const [portfolioValue, setPortfolioValue] = useState(0);
  const [userProfit, setUserProfit] = useState(0);
  const [openGainLoss, setOpenGainLoss] = useState(0);
  const [isLongTermInvestor, setIsLongTermInvestor] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [detailStock, setDetailStock] = useState({symbol: "",rating: 0,explanation:"", change: 0, changePercent: 0, currentPrice: 0, openPrice: 0, closePrice: 0, highPrice: 0, lowPrice: 0, volume: 0});

  useEffect(() => {
    refresh();
  },[]);
  
  const handleAddStock = async (e) => {
    e.preventDefault();
    if (!newStockSymbol.trim()) {
      setError("Please enter a valid stock or ETF symbol.");
      return;
    }
    const username = localStorage.getItem("loggedInUsername");

    try {
      const updatedWatchlist = await addStockToWatchlist(username, newStockSymbol);
      setWatchlist(updatedWatchlist);
    } catch (error) {
      setError("Failed to add stock to watchlist: " + error.message);
      alert("Failed to add stock to watchlist: " + error.message);
    }
  };


  const handleClickOpen = async (stock) => {;
    console.log(stock);
    setDialogOpen(true);
    stock.rating = await getStockRating(stock.symbol);
    stock.explanation = await getStockExplanation(stock.symbol);

    let history = await getStockHistory(stock.symbol);
    setPriceHistory(history["price"]);
    setDateList(history["date"]);


    console.log("Stock history:", priceHistory);
    console.log("Date list:", dateList);
    console.log(stock);
    setDetailStock(stock);
  };

  const handleClose = () => {
    setDialogOpen(false);
    let detail = detailStock;
    detail.rating = 0;
    detail.explanation = "";
    setDetailStock(detail);
  };

  const fetchProfit = async () => {
    const username = localStorage.getItem("loggedInUsername");
    try {
      let updatedProfit = await getUserProfit(username);
  
      updatedProfit = Number(parseFloat(updatedProfit).toFixed(3)); 
  
      setUserProfit(updatedProfit);
    } catch (error) {
      setError("Failed to get profit: " + error.message);
    }
  };

  const fetchPortfolioValue = async () => {
    const username = localStorage.getItem("loggedInUsername");
    try {
      let updatedPortfolioValue = await getPortfolioValue(username);

      updatedPortfolioValue = Number(parseFloat(updatedPortfolioValue).toFixed(3));
      setPortfolioValue(updatedPortfolioValue);
    } catch (error) {
      setError("Failed to get portfolio value: " + error.message);
    }
  }

  const getStockRecomendations = async (e) => {
    try {
      const updatedStockRecommendations = await getStockRequests(e);
      setStockRecommendations(updatedStockRecommendations);
    } catch (error) {
      setError("Failed to get stock recomendations: " + error.message);
    }
  };

  const conditionallyFormatStringBody2 = (value) => (
    <Typography variant="body2" sx={{ color: (String(value)).includes("-") ? "red" : "green" }}>
      {value}
    </Typography>
  );


  const conditionallyFormatStringH5 = (value) => (
    <Typography variant="h5" sx={{ color: (String(value)).includes("-") ? "red" : "green" }}>
      {value}
    </Typography>
  );

  const handleBuyStock = async (symbol, quantity, price) => {
    const username = localStorage.getItem("loggedInUsername");
    const roundedPrice = Math.round(parseFloat(price)) || 0;
    try {
      const updatedUser = await buyStock(username, quantity, symbol, roundedPrice);
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
      const updatedWatchlist = await removeStockFromWatchlist(username, e);
      setWatchlist(updatedWatchlist);
    } catch (error) {
      setError("Failed to add stock to watchlist: " + error.message);
    }
  };

  const refresh = async () => {
    const username = localStorage.getItem("loggedInUsername");
    await fetchPortfolioValue();
    await fetchProfit();
    await setLongTermInvestor(localStorage.getItem("loggedInUsername"), isLongTermInvestor);

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
      
      let openGainLoss = updatedPortfolioWithProfit.reduce((acc, stock) => acc + parseFloat(stock.profit), 0);
      openGainLoss = Number(parseFloat(openGainLoss).toFixed(3));
      setOpenGainLoss(openGainLoss);
      

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

  const handleChangeIvestorType = (event) => {
    setIsLongTermInvestor(event.target.checked);
    console.log("Switch is now:", event.target.checked);

  };

  const handlePriceChange = (index, value) => {
    const updatedWatchlist = [...watchlist];
    updatedWatchlist[index].price = value;
    setWatchlist(updatedWatchlist);
  };

  const handleSellStock = async (symbol, quantity) => {
    const username = localStorage.getItem("loggedInUsername");

    try {
      await sellStock(username, quantity, symbol); 

      const updatedPortfolio = await getStocks(username); 


      const updatedPortfolioWithProfit = await Promise.all(
        updatedPortfolio.map(async (stock) => {
          let profit = await getProfitOnStock(username, stock.symbol);
          profit = Number(parseFloat(profit).toFixed(3));
          return { ...stock, profit }; 
        })
      );

      setStocklist(updatedPortfolioWithProfit); 
    } catch (error) {
      setError("Failed to sell stock: " + error.message);
    }
  };

  const handleApiError = (error) => {
    console.error('API Error:', error);
    if (error.message?.includes('429')) {
        setError('API rate limit reached. Please try again in a minute.');
    } else {
        setError('An error occurred while fetching data. Please try again.');
    }
    setLoading(false);
  };

  const fetchStockData = async () => {
    setLoading(true);
    setError(null);
    try {
        const response = await fetch('/api/stocks/whatever');
        if (!response.ok) {
            if (response.status === 429) {
                throw new Error('429: Rate limit exceeded');
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setStocks(data || []); // Ensure we always set an array
    } catch (error) {
        handleApiError(error);
    } finally {
        setLoading(false);
    }
  };

  return (
    <div className="portfolio-container">


<Dialog open={dialogOpen} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>{detailStock.symbol}</DialogTitle>
        <DialogContent>

          <Stack direction="column" alignItems="center" divider={<Divider orientation="horizontal" flexItem />} spacing={0.5}>
          <Card variant="outlined" sx={{ width: "100%" }}>
            <CardContent>
            <Stack direction="row" spacing={1.5} alignItems="center">
              <Typography variant="h5" gutterBottom fontWeight="bold">
                WealthPlex rates this stock a
              </Typography>
              <Gauge width={140} height={140} value={detailStock.rating} valueMin={0} valueMax={10} sx={{".MuiGauge-valueText": {fontSize: "24px",fontWeight: "bold",color: "#0d47a1"}}}/>
            </Stack>
          </CardContent>
          </Card>
          <Card variant="outlined" sx={{ width: "100%" }}>
            <CardContent>
            <Stack direction="row" spacing={1.5} alignItems="center">
          <Stack direction="column" spacing={1.5} alignItems="center" divider={<Divider orientation="horizontal" flexItem />}>
          <Typography variant="h2" gutterBottom>{detailStock.currentPrice}</Typography>
          <Stack direction="row" divider={<Divider orientation="vertical" flexItem />} spacing={1}>
          <Typography variant="h5" gutterBottom>{detailStock.lowPrice}</Typography>
          <Typography variant="h5" gutterBottom>{detailStock.highPrice}</Typography>
          </Stack>

          </Stack>
          <LineChart
            xAxis={[
              { 
                data: dateList, 
                scaleType: "point"
              }
            ]}
            series={[
              { 
                data: priceHistory, 
                color: "#1976d2", 
                showMark: true, 
                stackedArea: true 
              }
            ]}
            width={600}
            height={300}
          />
          </Stack>
            </CardContent>
          </Card>
          <Card variant="outlined" sx={{ width: "100%" }}>
            <CardContent>
            <Stack direction="column" spacing={1.5} alignItems="center" divider={<Divider orientation="horizontal" flexItem />}>
            <Stack direction="row" divider={<Divider orientation="vertical" flexItem />} spacing={1}>
          <Typography variant="h5" gutterBottom>Change (%)</Typography>
          <Typography variant="h5" gutterBottom>{conditionallyFormatStringH5(String(detailStock.changePercent))}</Typography>
          <Typography variant="h5" gutterBottom>Volume</Typography>
          <Typography variant="h5" gutterBottom>{detailStock.volume}</Typography>
          </Stack>

          <Stack direction="row" divider={<Divider orientation="vertical" flexItem />} spacing={2}>
          <Typography variant="h5" gutterBottom>Open price</Typography>
          <Typography variant="h5" gutterBottom>{detailStock.openPrice}</Typography>
          <Typography variant="h5" gutterBottom>Close price</Typography>
          <Typography variant="h5" gutterBottom>{detailStock.closePrice}</Typography>
          </Stack>
            </Stack>


            </CardContent>
          </Card>
        
          <Card variant="outlined" sx={{ width: "100%" }}>
          <CardContent>
          <Typography variant="h5" gutterBottom>Here's what we think : {detailStock.explanation}</Typography>
            </CardContent>
            </Card>
          
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button onClick={handleClose} color="primary" variant="contained">
            Close
          </Button>
        </DialogActions>
      </Dialog>

      <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} sx={{ flexGrow: 1 }}>



        <Stack component={Paper} direction="row" divider={<Divider orientation="horizontal" flexItem />} spacing={4} sx={{
          justifyContent: "center",
          alignItems: "center",
          position: "relative",
          width: "100%",
          padding: 4,
          height: "auto"
        }}>


          <FormControlLabel
            control={<Switch checked={isLongTermInvestor} onChange={handleChangeIvestorType} />}
            label={isLongTermInvestor ? "Is long term investor" : "Is short term investor"} />
          <Typography variant="h2" gutterBottom>Current stats</Typography>
          <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={2}>
            <Typography variant="h4" gutterBottom>Porfolio Value</Typography>
            <Typography variant="h5" gutterBottom>{portfolioValue}</Typography>
          </Stack>

          <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={2}>
            <Typography variant="h4" gutterBottom>Profit</Typography>
            <Typography variant="h5" gutterBottom>{conditionallyFormatStringH5(userProfit.toString())}</Typography>
          </Stack>


          <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={2}>
            <Typography variant="h4" gutterBottom>Open/Gain Loss</Typography>
            <Typography variant="h5" gutterBottom>{conditionallyFormatStringH5(openGainLoss.toString())}</Typography>
          </Stack>



          <div >
            <form onChange={(event) => {
              setNewStockSymbol(event.target.value);
            }} onSubmit={handleAddStock}>
              <Stack direction="row" divider={<Divider orientation="vertical" flexItem />} spacing={4} padding={2}>
                <ButtonGroup variant="contained" aria-label="Basic button group">
                  <Button onClick={refresh}>Refresh</Button>
                  <Button type="submit">Add Stock</Button>
                </ButtonGroup>
                <Autocomplete
                  sx={{ minWidth: 300 }}
                  disableClearable
                  options={stockRecommendations || []}
                  onInputChange={(event, newInputValue) => {
                    setNewStockSymbol(newInputValue);
                  }}
                  renderInput={(params) => (
                    <TextField
                      onChange={(event) => {
                        getStockRecomendations(event.target.value);
                        setNewStockSymbol(event.target.value);
                      }}
                      {...params}
                      label="Enter Stock/ETF Symbol"
                      slotProps={{
                        input: {
                          ...params.InputProps,
                          type: 'search',
                        },
                      }}
                    />
                  )}
                />
              </Stack>
            </form>
          </div>

        </Stack>

        <div className="tables-container">
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              justifyContent: "center",
              alignItems: "center",
              minHeight: "100vh",
              overflow: "hidden",
              boxSizing: "border-box",
              flexGrow: 1,
            }}
          >
            <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={2}>
              <TableContainer component={Paper} sx={{ maxHeight: "50vh", overflowY: "auto" , width: "100%"}}>
                <Table sx={{
                 minWidth: "80%"
                }} aria-label="simple table">
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
                    {watchlist.length > 0 ? (
                      watchlist.map((stock, index) => (
                        <TableRow key={stock.symbol} sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
                          <TableCell component="th" scope="row">
                          <Button onClick={() => handleClickOpen(stock)}>{stock.symbol}</Button>
                          </TableCell>
                          <TableCell align="right">{conditionallyFormatStringBody2(stock.change.toString())}</TableCell>
                          <TableCell align="right">{conditionallyFormatStringBody2(stock.changePercent.toString())}</TableCell>
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
                              <Button variant="outlined" color="success" onClick={() => handleBuyStock(stock.symbol, stock.quantity, stock.price)}>
                                Buy
                              </Button>
                            </Stack>
                          </TableCell>
                          <TableCell align="right">
                            <Button variant="outlined" startIcon={<DeleteIcon />} color="error" onClick={() => handleRemoveStockFromWatchlist(stock.symbol)}>
                              Remove
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))
                    ) : (
                      <TableRow>
                        <TableCell colSpan={4} align="center">
                          {"No stocks in watchlist"}
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>

              <TableContainer component={Paper} sx={{ maxHeight: "50vh", overflowY: "auto" ,width: "100%"}}>
                <Table sx={{ minWidth: 900 }} aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>Stock Symbol</TableCell>
                      <TableCell align="right">Price</TableCell>
                      <TableCell align="right">Quantity</TableCell>
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
                          <TableCell align="right">{conditionallyFormatStringBody2(stock.profit)}</TableCell>
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
                              <Button variant="outlined" color="success" onClick={() => handleSellStock(stock.symbol, sellList[index])}>
                                Sell
                              </Button>
                            </Stack>
                          </TableCell>
                        </TableRow>
                      ))
                    ) : (
                      <TableRow>
                        <TableCell colSpan={4} align="center">
                          {"No stocks in portfolio"}
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </Stack>
          </Box>
        </div>

      </Stack>



      {error && <p className="error-message">{error}</p>}
    </div>
  );
}

export default Portfolio;