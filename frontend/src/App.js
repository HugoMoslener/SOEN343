import logo from './logo.svg';
import './App.css';
import { BrowserRouter as Router, Routes, Route, Link} from "react-router-dom";
import { useState, useEffect } from 'react';
import Login from './components/Login';
import Register from './components/Register';
import Home1 from './components/Home';
import Billing from './components/Billing';
import Pricing from './components/Pricing';
import RideHistory from './components/RideHistory';
import { authService } from './services/authService';
function Home({ user, onLogout }) {
  return (
    <div className="App">
      <header className="App-header">

        {user ? (
            <div className="min-h-screen w-full bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col">
                {/* Header */}
                <header className="bg-white shadow-sm py-4 px-8 flex justify-between items-center">
                    <h1 className="text-xl font-semibold text-gray-800">
                        Welcome back {localStorage.getItem("fullName")}
                    </h1>
                    <p className="text-sm text-gray-500">
                        Logged in as <span className="font-medium text-indigo-600">{ localStorage.getItem("username") || "Guest"}</span>
                    </p>
                </header>

                {/* Main Content */}
                <main className="flex-grow flex flex-col items-center justify-center text-center px-6">
                    <h2 className="text-3xl font-semibold text-gray-800 mb-3">
                        You’re successfully logged in!
                    </h2>
                    <p className="text-gray-600 max-w-md">
                        Explore the dashboard/map, the pricing, billing and the ride history.
                    </p>
                </main>

                {/* Footer */}
                <footer className="bg-white border-t border-gray-200 py-4 text-center text-sm text-gray-500">
                    © {new Date().getFullYear()} TopFounders. All rights reserved.
                </footer>
            </div>
        ) : (
            <div className="min-h-screen w-full bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col">
                {/* Header */}
                <header className="bg-white shadow-sm py-4 px-8 flex justify-between items-center">
                    <h1 className="text-xl font-semibold text-gray-800">
                        Welcome Dear Guest
                    </h1>
                </header>

                {/* Main Content */}
                <main className="flex-grow flex flex-col items-center justify-center text-center px-6">
                    <h2 className="text-3xl font-semibold text-gray-800 mb-3">
                        You’re in Guest Mode.
                    </h2>
                    <p className="text-gray-600 max-w-md">
                        Please Sign in to get Access to functionalities of this application.
                        If you do not have an account, create a new account using the "Register" option above
                    </p>
                </main>

                {/* Footer */}
                <footer className="bg-white border-t border-gray-200 py-4 text-center text-sm text-gray-500">
                    © {new Date().getFullYear()} TopFounders. All rights reserved.
                </footer>
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
        <nav className="bg-gradient-to-r from-slate-50 to-slate-100 border-b border-slate-200 shadow-sm">
            <div className="max-w-7xl mx-auto px-6 py-4">
                <div className="flex items-center justify-evenly w-full">
                    <Link
                        to="/"
                        className="text-slate-900 font-semibold text-lg hover:text-blue-600 transition-colors duration-200"
                    >
                        Home
                    </Link>

                    {!user && (
                        <>
                            <Link
                                to="/login"
                                className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 font-medium transition-all duration-200 shadow-sm hover:shadow-md"
                            >
                                Login
                            </Link>
                            <Link
                                to="/register"
                                className="bg-red-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 font-medium transition-all duration-200 shadow-sm hover:shadow-md"
                            >
                                Register
                            </Link>
                        </>
                    )}

                    <Link
                        to="/map"
                        className="text-slate-900 hover:text-blue-600 font-medium transition-colors duration-200"
                    >
                        Map
                    </Link>
                    <Link
                        to="/pricing"
                        className="text-slate-900 hover:text-blue-600 font-medium transition-colors duration-200"
                    >
                        Pricing
                    </Link>
                    {localStorage.getItem("role") === "rider" && (
                    <Link
                        to="/billing"
                        className="text-slate-900 hover:text-blue-600 font-medium transition-colors duration-200"
                    >
                        Billing
                    </Link>)}
                    {(localStorage.getItem("role") === "rider" || localStorage.getItem("role") === "operator"  )&& ( <Link
                        to="/ridehistory"
                        className="text-slate-900 hover:text-blue-600 font-medium transition-colors duration-200"
                    >
                        Ride History
                    </Link>)}
                    {user && (
                        <div className="text-slate-700 hover:text-blue-600 font-medium transition-colors duration-200">
                            <button
                                onClick={async () => {
                                    await handleLogout();
                                    window.location.href = "/login";
                                }}
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
                            </button> </div>)}
                </div>
            </div>
        </nav>

        <Routes>
        <Route path="/map" element=<Home1/>/>
          <Route path="/pricing" element=<Pricing/>/>
          <Route path="/billing" element=<Billing/>/>
          <Route path="/ridehistory" element=<RideHistory/>/>
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