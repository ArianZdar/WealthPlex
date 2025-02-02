import React from 'react';
import './investorType.css'; 
import { useNavigate } from 'react-router-dom';

function InvestmentType() {
  const navigate = useNavigate();

  const redirectToPortfolio = () => {
    navigate('/portfolio'); 
  };

  return (
    <div className="investment-type-container">
      <h1 className="investment-title">What type of investments are you looking for?</h1>
      <div className="button-container">
        {/* Attach the same redirect handler to both buttons */}
        <button 
          className="investment-button short-term-button" 
          onClick={redirectToPortfolio}
        >
          Short Term
        </button>
        <button 
          className="investment-button long-term-button" 
          onClick={redirectToPortfolio}
        >
          Long Term
        </button>
      </div>
    </div>
  );
}

export default InvestmentType;
