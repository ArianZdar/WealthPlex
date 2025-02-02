import React, {useState, useEffect} from 'react';
import './home.css';
import Greeting from '../../components/greeting';
import { useNavigate } from 'react-router-dom';


function Home() {
  const navigate = useNavigate();
  const [darkMode, setDarkMode] = useState(false);
  const [date, setDate] = useState("");

  useEffect(() => {
    const today = new Date();
    setDate(today.toDateString());
  }, []);

  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
    document.body.classList.toggle('dark-mode');
  };

  return (
    <div className="container">
      <h1>Welcome To Your Stock Market Analyzer </h1>
      <br />
      <h2 className="date-display">📅 {date} 📅</h2>
      <br />
      <p className="first-para">Search for stocks, analyze trends, and manage your portfolio with ease.</p>
      <br />
      <p className="sec-para">Maximize Your Wealth Make your money work harder with intelligent investment solutions and expert guidance tailored to your goals. Grow your wealth with confidence and long-term success.</p>

      {/* Trending Stocks (Mock Data for Now) */}
      <h2 className="market-trend-title">📈 Market Trends 📈</h2>
      <div className="features-container">
        <div className="feature">
          <h3><span className="highlight">Low fees</span> meet higher yields</h3>
          <p>Your money’s always making more with low-fee investing and high-interest savings.</p>
        </div>
        <div className="feature">
          <h3>Unmatched access</h3>
          <p>Get sophisticated investment opportunities traditionally reserved for industry insiders and the ultra-wealthy.</p>
        </div>
        <div className="feature">
          <h3>Smart & simple</h3>
          <p>In just a few taps, set your financial goals in motion, and let our easy-to-use products handle the rest.</p>
        </div>
      </div>




      <button className="button" onClick={() => navigate('/login')}>Get Started</button>

    </div>
  );
}

export default Home;
