import React from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../../../shared/api/axios.js';
import './LogoutButton.css';

const LogoutButton = () => {
    const navigate = useNavigate();

    const logout = async () => {
        try {
            await axios.post('/api/auth/logout');
        } catch (error) {
            console.error('Logout failed', error);
        } finally {
            localStorage.removeItem('accessToken');
            navigate('/');
        }
    };

    return (
        <button className="logout-btn" onClick={logout}>
            Logout
        </button>
    );
};

export default LogoutButton;
