import logo from './logo.svg';
import './App.css';
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import { useState, useEffect } from 'react';
import Login from './ui/login/Login';
import Register from './ui/login/Register';
import Home1 from './ui/dashboard/Home';
import { authService } from './services/authService';

function Home({ user, onLogout }) {
  return (
    <div className="App">
      <header className="App-header">

        <p>Welcome to SOEN 343 Project</p>
        user:  {localStorage.getItem("address")}
        {user ? (
          <div>
            <p>Hello, {user.email}!</p>
            <p>You are logged in!</p>
            <button 
              onClick={onLogout}
              style={{
                padding: '10px 20px',
                backgroundColor: '#dc3545',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              Logout
            </button>
          </div>
        ) : (
          <div>
            <p>Please login to continue</p>
            <div style={{ marginTop: '20px' }}>
              <Link 
                to="/login" 
                style={{
                  marginRight: '10px',
                  padding: '10px 20px',
                  backgroundColor: '#007bff',
                  color: 'white',
                  textDecoration: 'none',
                  borderRadius: '4px'
                }}
              >
                Login
              </Link>
              <Link 
                to="/register"
                style={{
                  padding: '10px 20px',
                  backgroundColor: '#28a745',
                  color: 'white',
                  textDecoration: 'none',
                  borderRadius: '4px'
                }}
              >
                Register
              </Link>
            </div>
          </div>
        )}
      </header>
    </div>
  );
}

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const unsubscribe = authService.onAuthStateChanged((user) => {
      setUser(user);

    });

    return () => unsubscribe();
  }, []);

  const handleLogout = async () => {
    await authService.logout();
    localStorage.clear();
    setUser(null);
  };

  return (
    <Router>
      <nav style={{ padding: '10px', backgroundColor: '#f8f9fa' }}>
        <Link to="/" style={{ marginRight: '20px', textDecoration: 'none' }}>Home</Link>
        {!user && (
          <>
            <Link to="/login" style={{ marginRight: '20px', textDecoration: 'none' }}>Login</Link>
            <Link to="/register" style={{ textDecoration: 'none' }}>Register</Link>
          </>
        )}
          <Link to="/map" style={{ marginRight: '20px',marginLeft: '20px', textDecoration: 'none' }}>Map</Link>
      </nav>

      <Routes>
        <Route path="/map" element=<Home1/>/>
        <Route path="/" element={<Home user={user} onLogout={handleLogout} />} />
          <Route path="/home" element={<Home user={user} onLogout={handleLogout} />} />
        <Route path="/login" element={
          <Login onLogin={(user) => setUser(user)} />
        } />
        <Route path="/register" element={
          <Register onRegister={(user) => setUser(user)} />
        } />
      </Routes>
    </Router>
  );
}

export default App;