import React from 'react';
import './UserProfile.css';

const UserProfile = ({ user }) => {
    if (!user) return null;

    return (
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
        </div>
    );
};

export default UserProfile;
