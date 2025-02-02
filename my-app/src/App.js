import React from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Greeting from './components/greeting';
import Home from './pages/homepage/home';
import Portfolio from './pages/portfoliopage/portfolio';
import Login from './pages/loginpage/login';
import StockInfo from './pages/stockinfopage/stockinfo';
import Register from './pages/RegistrationPage/registration';
import InvestorType from './pages/investorTypePage/investorType';


function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/portfolio" element={<Portfolio />} />
        <Route path="/login" element={<Login />} />
        <Route path="/registration" element={<Register />} />
        <Route path="/typeInvestor" element={<InvestorType />} />
        <Route path="/stockinfo/:symbol" element={<StockInfo />} />
      </Routes>
    </Router>
  );
}

export default App;
