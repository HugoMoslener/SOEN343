import React, { useState } from 'react';
import { authService } from '../services/authService';
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
        setTimeout(() => {
            window.location.reload();
        }, 100);

    } else {
      setError(result.error);
    }
    
    setLoading(false);
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 p-4">
          <div className="w-full max-w-md">
              <div className="bg-white rounded-2xl shadow-xl p-8 space-y-6">
                  {/* Header */}
                  <div className="text-center space-y-2">
                      <h1 className="text-3xl font-bold text-gray-900">Welcome Back</h1>
                      <p className="text-gray-600">Sign up to create your account</p>
                  </div>

                  {/* Error Message */}
                  {error && (
                      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>
                  )}

                  {/* Form */}
                  <form onSubmit={handleSubmit} className="space-y-5">
                      {/* Email Input */}
                      <div className="space-y-2">
                          <label htmlFor="username" className="block text-sm font-medium text-gray-700">
                              Username
                          </label>
                          <input
                              id="username"
                              type="text"
                              value={username}
                              onChange={(e) => setUsername(e.target.value)}
                              required
                              placeholder="Enter your username"
                              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all outline-none placeholder:text-gray-400"
                          />
                      </div>

                      {/* Password Input */}
                      <div className="space-y-2">
                          <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                              Password
                          </label>
                          <input
                              id="password"
                              type="password"
                              value={password}
                              onChange={(e) => setPassword(e.target.value)}
                              required
                              placeholder="Enter your password"
                              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all outline-none placeholder:text-gray-400"
                          />
                      </div>

                      {/* Submit Button */}
                      <button
                          type="submit"
                          disabled={loading}
                          className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200 shadow-md hover:shadow-lg disabled:cursor-not-allowed"
                      >
                          {loading ? "Signing in..." : "Sign In"}
                      </button>
                  </form>
                  <div className="mt-6 text-center">
                      <p className="text-sm text-slate-600">
                          Don't have an account?{" "}
                          <a href="/Register" className="text-blue-600 hover:text-blue-700 font-medium">
                              Sign up
                          </a>
                      </p>
                  </div>
              </div>
          </div>
      </div>
  );
};

export default Login;