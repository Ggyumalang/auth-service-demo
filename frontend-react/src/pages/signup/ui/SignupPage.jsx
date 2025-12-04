import React from 'react';
import { Link } from 'react-router-dom';
import SignupForm from '../../../features/auth/signup/ui/SignupForm';
import './SignupPage.css';

const SignupPage = () => {
    return (
        <div className="signup-container">
            <div className="signup-card">
                <h1>Create Account</h1>
                <p className="subtitle">Join us today</p>

                <SignupForm />

                <div className="login-link">
                    Already have an account?{' '}
                    <Link to="/">Login</Link>
                </div>
            </div>
        </div>
    );
};

export default SignupPage;
