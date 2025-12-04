import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../../../shared/api/axios.js';
import './SignupForm.css';

const SignupForm = () => {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSignup = async (e) => {
        e.preventDefault();
        try {
            await axios.post('/api/auth/signup', {
                username,
                email,
                password
            });
            navigate('/');
        } catch (err) {
            setError(err.response?.data?.message || 'Signup failed');
        }
    };

    return (
        <form onSubmit={handleSignup} className="signup-form">
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
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
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
                Sign Up
            </button>
        </form>
    );
};

export default SignupForm;
