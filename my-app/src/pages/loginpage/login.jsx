import './login.css'; 
import { FaRegUserCircle } from "react-icons/fa";
import { FaLock } from "react-icons/fa";
import { Link } from 'react-router-dom';
import { login } from '../../assets/utils/userRequests'
import React, { useState } from 'react';

const Login = () => {
    // State to store the username/email and password
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loggedInUsername, setLoggedInUsername] = useState(''); // Use state for logged-in username
  
    // Function to handle the login button click
    const handleLogin = async (e) => {
        e.preventDefault(); // Prevent form submission refresh
        try {
            const response = await login(username, password); // Call the login function with actual username/password
            if (response) {
                setLoggedInUsername(username); // Set logged-in username in state
                console.log("Login successful:", response);
                // Handle successful login, redirect user or display message
            } else {
                console.log("Login failed: Invalid credentials");
            }
        } catch (error) {
            console.error("Login failed:", error.message);
        }
    };
  
    return (
      <div className="login-page">
        <div className="wrapper">
          <form onSubmit={handleLogin}>
            <h1>Login to WealthPlex</h1>
            <div className="input-box">
              <input
                type="text"
                placeholder="Username/Email Address"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
              <FaRegUserCircle className="icon" />
            </div>
            <div className="input-box">
              <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <FaLock className="icon" />
            </div>
            <div className="remember-forgot">
              <label>
                <input type="checkbox" /> Remember me
              </label>
            </div>
            <button type="submit">Login</button>
            <div className="register-link">
              <p>
                Don't have an account? <Link to="/registration">Register</Link>
              </p>
            </div>
          </form>
        </div>
      </div>
    );
};
  
export default Login;
