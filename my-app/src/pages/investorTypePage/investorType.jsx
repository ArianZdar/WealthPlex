import React from 'react';
import './investorType.css'; 
import { useNavigate } from 'react-router-dom';
import { setLongTermInvestor } from '../../assets/utils/userRequests';

function InvestmentType() {
  const navigate = useNavigate();

  const redirectToPortfolio = () => {
    navigate('/portfolio'); 
  };


  const handleSetInvestmentType = async (e) => {
    const username = localStorage.getItem("loggedInUsername");

    try {
      const updatedUser = await setLongTermInvestor(username, e);
    } catch (error) {
      console.error("Failed to set investment type:", error.message);
    }
  };

  return (
    <div className="investment-type-container">
      <h1 className="investment-title">What type of investments are you looking for?</h1>
      <div className="button-container">
        {/* Attach the same redirect handler to both buttons */}
        <button 
          className="investment-button short-term-button" 
          onClick={() => {
            handleSetInvestmentType(false);
            redirectToPortfolio();
          }}
        >
          Short Term
        </button>
        <button 
          className="investment-button long-term-button" 
          onClick={() => {
            handleSetInvestmentType(true);
            redirectToPortfolio();
          }}
        >
          Long Term
        </button>
      </div>
    </div>
  );
}

export default InvestmentType;
