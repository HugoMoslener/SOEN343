import React, { useState } from 'react';
import { authService } from '../../services/authService';
import { useNavigate } from 'react-router-dom';



const Login = ({ onLogin }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
   const navigate = useNavigate();

  const fetchUser = async () => {
      try {
        const response = await fetch("/api/signIn/getUserData", {
          method: "POST",
          headers: { "Content-Type": "text/plain" },
          body:username, // send username as JSON string
        });

        const result = await response.json();
        localStorage.setItem("username", result.username);
        localStorage.setItem("address", result.address);
        localStorage.setItem("role", result.role);
        localStorage.setItem("email", result.email);
        localStorage.setItem("fullName", result.fullName);

        if(result.role === "rider"){
         localStorage.setItem("paymentInformation", result.paymentInformation);
        }
      } catch (err) {
        console.error(err);
      }
    };


  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    const firebaseEmail = username + "@TopFounders.com";
    const result = await authService.login(firebaseEmail, password);
    
    if (result.success) {
      await fetchUser();
      onLogin(result.user);

      navigate('/');

    } else {
      setError(result.error);
    }
    
    setLoading(false);
  };

  return (
    <div style={{ maxWidth: '400px', margin: '0 auto', padding: '20px' }}>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <label>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
        {error && <div style={{ color: 'red', marginBottom: '15px' }}>{error}</div>}
        <button 
          type="submit" 
          disabled={loading}
          style={{ 
            width: '100%', 
            padding: '10px', 
            backgroundColor: '#007bff', 
            color: 'white', 
            border: 'none',
            borderRadius: '4px'
          }}
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
};

export default Login;