import React from 'react';
import { Link } from 'react-router-dom';

const LoginFailurePage = () => {
    return (
        <div style={{ textAlign: 'center', paddingTop: '5rem' }}>
            <h2 style={{ color: '#ff4d4d' }}>Login Failed</h2>
            <p>Something went wrong during authentication.</p>
            <Link to="/" style={{ color: '#4a90e2', textDecoration: 'none' }}>Back to Login</Link>
        </div>
    );
};

export default LoginFailurePage;
