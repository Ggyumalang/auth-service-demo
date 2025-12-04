import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import './LoginSuccess.css';

const LoginSuccess = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');

    useEffect(() => {
        if (token) {
            localStorage.setItem('accessToken', token);
        }

        const timer = setTimeout(() => {
            navigate('/home');
        }, 500);

        return () => clearTimeout(timer);
    }, [token, navigate]);

    return (
        <div className="success-container">
            <h2>Login Successful!</h2>
            <p>Redirecting you...</p>
        </div>
    );
};

export default LoginSuccess;
