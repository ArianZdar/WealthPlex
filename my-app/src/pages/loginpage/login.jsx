import './login.css'; 
import { FaRegUserCircle } from "react-icons/fa";
import { FaLock } from "react-icons/fa";
import { Link } from 'react-router-dom';
import { login } from '../../assets/utils/userRequests';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    const navigate = useNavigate(); 
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await login(username, password); 
            if (response) {
                console.log("Login successful:", response);
                localStorage.setItem("loggedInUsername", username);
                navigate("/portfolio"); // Navigate to the portfolio page after successful login
            } else {
                console.log("Login failed: Invalid credentials");
                alert("Invalid username or password. Please try again.");
            }
        } catch (error) {
            console.error("Login failed:", error.message);
            alert("An error occurred during login. Please try again.");
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
