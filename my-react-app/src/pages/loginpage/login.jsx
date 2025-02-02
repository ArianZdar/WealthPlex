import React from 'react';
import './login.css';

const Login = () => {
    return (
        <div className='login-container'>
            <div className='login-card'>
                <h1 className='login-title'>Login
                    <span className='title'> into WealthPlex  </span>
                </h1>
                <form className='login-form'>
                    <div>
                        <label className="login-label">
                            <span> Username </span>
                        </label>
                        <input type="text" placeholder=' Enter Username ' className='login-input'/>
                    </div>
                    <div>
                        <label className='login-label'>
                            <span >Password</span>
                        </label>
                        <input type="text" placeholder=' Enter Password ' className='login-input'/>
                    </div>

                    <div>
                        <button className='login-button'>Login</button>
                    </div>
                </form>
            </div>

        </div>
    )

};

export default Login;