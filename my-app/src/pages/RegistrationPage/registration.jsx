import React from "react";
import './registration.css'; 
import { FaRegUserCircle, FaLock } from "react-icons/fa";
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';


const Registration = () => {
    const navigate = useNavigate(); 

    const handleRegister = (e) => {
        e.preventDefault(); 
        navigate("/portfolio"); 
    };

    return (
        <div className="registration-page">
            <div className="registration-wrapper"> 
                <form onSubmit={handleRegister}>
                    <h1>Register for WealthPlex</h1>
                    <div className="registration-input-box">
                        <input type="text" placeholder="Username/Email Address" required />
                        <FaRegUserCircle className="icon" />
                    </div>
                    <div className="registration-input-box">
                        <input type="password" placeholder="Password" required />
                        <FaLock className="icon" />
                    </div>
                    <div className="registration-input-box">
                        <input type="password" placeholder="Confirm Password" required />
                        <FaLock className="icon" />
                    </div>
                    <button type="submit" className="registration-button">Register</button>
                    
                    <div className="login-link">
                        <p>Already have an account? <Link to="/login">Login</Link></p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Registration;
