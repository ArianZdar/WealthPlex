
import './registration.css'; 
import { FaRegUserCircle, FaLock } from "react-icons/fa";
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import {signup} from '../../assets/utils/userRequests'
import React, { useState } from 'react';


const Registration = () => {
    const navigate = useNavigate(); 

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const LoggedInUsername = "";


    const handleRegister = async () => {
        try {
        await signup(username, password);
    } catch (error) {
        console.error("Login failed:", error.message);
    }
    };

    return (
        <div className="registration-page">
            <div className="registration-wrapper"> 
                <form onSubmit={handleRegister}>
                    <h1>Register for WealthPlex</h1>
                    <div className="registration-input-box">
                        <input type="text" 
                        placeholder="Username/Email Address" required 
                        onChange={(e) => setUsername(e.target.value)}/>
                        <FaRegUserCircle className="icon" />
                    </div>
                    <div className="registration-input-box">
                        <input type="password" placeholder="Password" required />
                        <FaLock className="icon" />
                    </div>
                    <button type="submit" className="registration-button"
                    onClick={handleRegister}>Register</button>
                    
                    <div className="login-link">
                        <p>Already have an account? <Link to="/login">Login</Link></p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Registration;
