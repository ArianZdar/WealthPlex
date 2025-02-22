import React from "react";
import FetchData from "../utils/FetchData"; 

function stocks() {
  const { data, loading, error } = FetchData("/api/stocks/info/VOO");

  if (loading) return <p>Loading stock data...</p>;
  if (error) return <p>Error fetching stock data: {error}</p>;
  if (!data) return <p>No data available.</p>;

  return (
    <div>
      <h1>Stock Data for {data.symbol}</h1>
      <p><strong>Open Price:</strong> {data.openPrice}</p>
      <p><strong>Current Price:</strong> {data.currentPrice}</p>
      <p><strong>Close Price:</strong> {data.closePrice}</p>
      <p><strong>High Price:</strong> {data.highPrice}</p>
      <p><strong>Low Price:</strong> {data.lowPrice}</p>
      <p><strong>Volume:</strong> {data.volume}</p>
      <p><strong>Change:</strong> {data.change}</p>
      <p><strong>Change (%):</strong> {data.changePercent}</p>
    </div>
  );
}

export default stocks;
