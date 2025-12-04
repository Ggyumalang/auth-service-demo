import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../../../shared/api/axios.js';
import './LoginForm.css';

const LoginForm = () => {
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

    return (
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
    );
};

export default LoginForm;
