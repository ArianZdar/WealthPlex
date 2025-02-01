import React from 'react';
import './home.css';
import Greeting from '../../components/greeting';

function Home() {
  return (
    <div className="container">
      <Greeting></Greeting>
      <h1>Welcome to Stock Market Analyzer 🚀</h1>
      <p>
        Search for stocks, analyze trends, and manage your portfolio with ease.
      </p>
      <button className="button">Get Started</button>
    </div>
  );
}

export default Home;
