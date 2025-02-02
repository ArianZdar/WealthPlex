import React from 'react';
import './investorType.css'; // Ensure the correct CSS file is imported

function InvestmentType() {
  return (
    <div className="investment-type-container">
      <h1 className="investment-title">What type of investments are you looking for?</h1>
      <div className="button-container">
        <button className="investment-button short-term-button">Short Term</button>
        <button className="investment-button long-term-button">Long Term</button>
      </div>
    </div>
  );
}

export default InvestmentType; // Make sure it's exported as default
