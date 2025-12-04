import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from '../api/axios';
import './Login.css';

const Login = () => {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('/api/auth/login', {
                username,
                password
            });

            if (response.data && response.data.accessToken) {
                localStorage.setItem('accessToken', response.data.accessToken);
            }

            navigate('/home');
        } catch (err) {
            setError('Invalid username or password');
        }
    };

    const loginWithKakao = () => {
        window.location.href = 'http://localhost:8080/oauth2/authorization/kakao';
    };

    const loginWithNaver = () => {
        window.location.href = 'http://localhost:8080/oauth2/authorization/naver';
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <h1>Welcome Back</h1>
                <p className="subtitle">Sign in to continue</p>

                <form onSubmit={handleLogin} className="login-form">
                    <div className="form-group">
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    {error && <div className="error-message">{error}</div>}

                    <button type="submit" className="submit-btn">
                        Sign In
                    </button>
                </form>

                <div className="divider">
                    <span>OR</span>
                </div>

                <div className="button-group">
                    <button className="kakao-btn" onClick={loginWithKakao}>
                        <span className="icon">💬</span> Login with Kakao
                    </button>
                    <button className="naver-btn" onClick={loginWithNaver}>
                        <span className="icon">N</span> Login with Naver
                    </button>
                </div>

                <div className="signup-link">
                    Don't have an account?{' '}
                    <Link to="/signup">Sign Up</Link>
                </div>
            </div>
        </div>
    );
};

export default Login;
