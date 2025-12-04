import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../../shared/api/axios';
import UserProfile from '../../../entities/user/ui/UserProfile';
import LogoutButton from '../../../features/auth/logout/ui/LogoutButton';
import './HomePage.css';

const HomePage = () => {
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

    if (loading) {
        return <div className="home-container"><div className="loading">Loading...</div></div>;
    }

    return (
        <div className="home-container">
            <UserProfile user={user} />
            {user && <LogoutButton />}
        </div>
    );
};

export default HomePage;
