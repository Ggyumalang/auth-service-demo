import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../api/axios';
import './Home.css';

const Home = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await axios.get('/api/user');
                setUser(response.data);
            } catch (error) {
                console.error('Failed to fetch user', error);
                navigate('/');
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, [navigate]);

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

    if (loading) {
        return <div className="home-container"><div className="loading">Loading...</div></div>;
    }

    if (!user) {
        return null;
    }

    return (
        <div className="home-container">
            <div className="user-card">
                <div className="avatar-placeholder">
                    {user.name ? user.name[0] : 'U'}
                </div>
                <h2>Hello, {user.name || 'User'}!</h2>
                <div className="user-details">
                    {Object.entries(user).map(([key, value]) => (
                        <p key={key}>
                            <strong>{key}:</strong> {String(value)}
                        </p>
                    ))}
                </div>
                <button className="logout-btn" onClick={logout}>
                    Logout
                </button>
            </div>
        </div>
    );
};

export default Home;
