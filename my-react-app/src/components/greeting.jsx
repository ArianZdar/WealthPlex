import React from 'react';
import './greeting.css'; // Import the CSS file

function Greeting({ name }) {
  return (
    <div className="greeting-container">
      <p>We're glad to have you here. Explore WealthPlex and have fun (make profits)!</p>
      <a href="/explore" className="explore-button">Explore Now</a> {/* Optional Button */}
    </div>
  );
}

export default Greeting;