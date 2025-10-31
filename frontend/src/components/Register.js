import React, { useState } from 'react';
import { authService } from '../services/authService';
import { useNavigate } from 'react-router-dom';

const Register = ({ onRegister }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [username,setUsername] = useState('');
  const [paymentInformation,setPaymentInformation] = useState('');
  const [address,setAddress] = useState('');
  const [fullName,setFullName] = useState('');
  const [role,setRole] = useState('rider');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');



    if (password !== confirmPassword) {
      setError('Passwords do not match');
      setLoading(false);
      return;
    }
    if(email !== '' & password !== '' & confirmPassword !== '' & username !== '' & paymentInformation !== '' & address !== '' & fullName !== '' ){
    const firebaseEmail = username + "@TopFounders.com";
    const result = await authService.register(firebaseEmail, password);
    
    if (result.success) {
      onRegister(result.user);


      const userData = {
         email: email,
         username: username,
         paymentInformation:paymentInformation,
         address:address,
         fullName: fullName,
       };


       const response = await fetch('api/signIn/saveRider', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
       });
        setAddress('');
             setFullName('');
             setUsername('');
             setConfirmPassword('');
             setPassword('');
             setEmail('');
             setPaymentInformation('');
             navigate('/login');
    } else {
      setError(result.error);
    }}
    
    setLoading(false);
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100 p-4">
          <div className="w-full max-w-md">
              <div className="bg-white rounded-2xl shadow-xl p-8 border border-slate-200">
                  <div className="text-center mb-8">
                      <h2 className="text-3xl font-bold text-slate-900 mb-2">Create Account</h2>
                      <p className="text-slate-600 text-sm">Join us today and get started</p>
                  </div>

                  <form onSubmit={handleSubmit} className="space-y-5">
                      <div>
                          <label htmlFor="email" className="block text-sm font-medium text-slate-700 mb-2">
                              Email Address
                          </label>
                          <input
                              id="email"
                              type="email"
                              value={email}
                              onChange={(e) => setEmail(e.target.value)}
                              required
                              className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none text-slate-900 placeholder:text-slate-400"
                              placeholder="johndoe@example.com"
                          />
                      </div>

                      <div>
                          <label htmlFor="username" className="block text-sm font-medium text-slate-700 mb-2">
                              Username
                          </label>
                          <input
                              id="username"
                              type="text"
                              value={username}
                              onChange={(e) => setUsername(e.target.value)}
                              required
                              className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none text-slate-900 placeholder:text-slate-400"
                              placeholder="johndoe"
                          />
                      </div>

                      <div>
                          <label htmlFor="fullName" className="block text-sm font-medium text-slate-700 mb-2">
                              Full Name
                          </label>
                          <input
                              id="fullName"
                              type="text"
                              value={fullName}
                              onChange={(e) => setFullName(e.target.value)}
                              required
                              className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none text-slate-900 placeholder:text-slate-400"
                              placeholder="John Doe"
                          />
                      </div>

                      <div>
                          <label htmlFor="address" className="block text-sm font-medium text-slate-700 mb-2">
                              Address
                          </label>
                          <input
                              id="address"
                              type="text"
                              value={address}
                              onChange={(e) => setAddress(e.target.value)}
                              required
                              className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none text-slate-900 placeholder:text-slate-400"
                              placeholder="123 JohnDoe St, City,Doe State"
                          />
                      </div>

                      <div>
                          <label htmlFor="paymentInformation" className="block text-sm font-medium text-slate-700 mb-2">
                              Payment Information
                          </label>
                          <input
                              id="paymentInformation"
                              type="text"
                              value={paymentInformation}
                              onChange={(e) => setPaymentInformation(e.target.value)}
                              required
                              className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none text-slate-900 placeholder:text-slate-400"
                              placeholder="Card details or payment method"
                          />
                      </div>

                      <div>
                          <label htmlFor="password" className="block text-sm font-medium text-slate-700 mb-2">
                              Password
                          </label>
                          <input
                              id="password"
                              type="password"
                              value={password}
                              onChange={(e) => setPassword(e.target.value)}
                              required
                              className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none text-slate-900 placeholder:text-slate-400"
                              placeholder="••••••••"
                          />
                      </div>

                      <div>
                          <label htmlFor="confirmPassword" className="block text-sm font-medium text-slate-700 mb-2">
                              Confirm Password
                          </label>
                          <input
                              id="confirmPassword"
                              type="password"
                              value={confirmPassword}
                              onChange={(e) => setConfirmPassword(e.target.value)}
                              required
                              className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none text-slate-900 placeholder:text-slate-400"
                              placeholder="••••••••"
                          />
                      </div>

                      {error && (
                          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>
                      )}

                      <button
                          type="submit"
                          disabled={loading}
                          className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-slate-400 disabled:cursor-not-allowed text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200 shadow-lg shadow-blue-500/30 hover:shadow-xl hover:shadow-blue-500/40"
                      >
                          {loading ? "Creating Account..." : "Register"}
                      </button>
                  </form>

                  <div className="mt-6 text-center">
                      <p className="text-sm text-slate-600">
                          Already have an account?{" "}
                          <a href="/Login" className="text-blue-600 hover:text-blue-700 font-medium">
                              Sign in
                          </a>
                      </p>
                  </div>
              </div>
          </div>
      </div>
  );
};

export default Register;