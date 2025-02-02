import React, {useEffect} from 'react';
import './home.css';
import Greeting from '../../components/greeting';
import { useNavigate } from 'react-router-dom';


function Home() {
  const navigate = useNavigate();

  

  return (
    <div className="container">
      <Greeting />
      <h1>Welcome To Your Stock Market Analyzer 🚀</h1>
      <p>
        Search for stocks, analyze trends, and manage your portfolio with ease.
      </p>
      <button className="button" onClick={() => navigate('/login')}>Get Started</button>
    </div>
  );
}

export default Home;
