import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Login from './views/Login';
import Signup from './views/Signup';
import Home from './views/Home';
import LoginSuccess from './views/LoginSuccess';
import LoginFailure from './views/LoginFailure';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/home" element={<Home />} />
      <Route path="/login/success" element={<LoginSuccess />} />
      <Route path="/login/failure" element={<LoginFailure />} />
    </Routes>
  );
}

export default App;
