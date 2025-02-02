import React from "react";
import './login.css'; 
import { FaRegUserCircle } from "react-icons/fa";
import { FaLock } from "react-icons/fa";
import { FcGoogle } from "react-icons/fc";
import background from '../../assets/background.png';
import { Link } from 'react-router-dom';



const Login = ()=>{
    return (
        <div className="login-page">
            <div className='wrapper'> 
                <form action="">
                    <h1>Login to WealthPlex</h1>
                    <div className="input-box">
                        <input type="text" placeholder='Username/Email Address' required />
                        <FaRegUserCircle className='icon'/>
                    </div>
                    <div className="input-box">
                        <input type="password" placeholder='Password' required />
                        <FaLock className='icon'/>
                    </div>
                    <div className="remember-forgot">
                        <label><input type="checkbox" /> Remember me</label>
                        <a href="#"> Forgot password?</a>
                    </div>
                    <button type="submit"> Login</button>
                    
                    <div className="register-link">
                        <p> Don't have an account? <Link to="/registration">Register</Link></p>
                    </div>

                </form>
            </div>
            </div>
    );
};

export default Login;