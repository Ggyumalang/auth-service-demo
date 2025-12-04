import React from 'react';
import { Link } from 'react-router-dom';
import LoginForm from '../../../features/auth/login/ui/LoginForm';
import './LoginPage.css';

const LoginPage = () => {
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

                <LoginForm />

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

export default LoginPage;
