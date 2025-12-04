import React from 'react';
import { Routes, Route } from 'react-router-dom';
import LoginPage from '../pages/login/ui/LoginPage';
import SignupPage from '../pages/signup/ui/SignupPage';
import HomePage from '../pages/home/ui/HomePage';
import LoginSuccessPage from '../pages/login-success/ui/LoginSuccessPage';
import LoginFailurePage from '../pages/login-failure/ui/LoginFailurePage';

function App() {
    return (
        <Routes>
            <Route path="/" element={<LoginPage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/login/success" element={<LoginSuccessPage />} />
            <Route path="/login/failure" element={<LoginFailurePage />} />
        </Routes>
    );
}

export default App;
