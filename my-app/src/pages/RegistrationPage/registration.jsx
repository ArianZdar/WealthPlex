import './registration.css'; 
import { FaRegUserCircle, FaLock } from "react-icons/fa";
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { signup } from '../../assets/utils/userRequests';
import React, { useState } from 'react';

const Registration = () => {
    const navigate = useNavigate(); 

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleRegister = async (e) => {
        e.preventDefault(); // Prevent default form submission
        try {
            const response = await signup(username, password);
            if (response) {
                console.log("Registration successful:", response);
                navigate('/InvestmentType'); // Redirect to the investor page after successful registration
            } else {
                console.error("Registration failed: Invalid data");
                alert("Registration failed. Please try again.");
            }
        } catch (error) {
            console.error("Registration failed:", error.message);
            alert("An error occurred during registration. Please try again.");
        }
    };

    return (
        <div className="registration-page">
            <div className="registration-wrapper"> 
                <form onSubmit={handleRegister}>
                    <h1>Register for WealthPlex</h1>
                    <div className="registration-input-box">
                        <input 
                            type="text" 
                            placeholder="Username/Email Address" 
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required 
                        />
                        <FaRegUserCircle className="icon" />
                    </div>
                    <div className="registration-input-box">
                        <input 
                            type="password" 
                            placeholder="Password" 
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required 
                        />
                        <FaLock className="icon" />
                    </div>
                    <button type="submit" className="registration-button">
                        Register
                    </button>
                    
                    <div className="login-link">
                        <p>Already have an account? <Link to="/login">Login</Link></p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Registration;
