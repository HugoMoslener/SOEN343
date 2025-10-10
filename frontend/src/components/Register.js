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
    <div style={{ maxWidth: '400px', margin: '0 auto', padding: '20px' }}>
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
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
           <label>FullName:</label>
             <input
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                    style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                  />
                </div>
         <div style={{ marginBottom: '15px' }}>
                          <label>Address:</label>
                          <input
                            type="text"
                            value={address}
                            onChange={(e) => setAddress(e.target.value)}
                            required
                            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                          />
                        </div>
         <div style={{ marginBottom: '15px' }}>
                          <label>Payment Information:</label>
                          <input
                            type="text"
                            value={paymentInformation}
                            onChange={(e) => setPaymentInformation(e.target.value)}
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
        <div style={{ marginBottom: '15px' }}>
          <label>Confirm Password:</label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
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
            backgroundColor: '#28a745', 
            color: 'white', 
            border: 'none',
            borderRadius: '4px'
          }}
        >
          {loading ? 'Creating Account...' : 'Register'}
        </button>
      </form>
    </div>
  );
};

export default Register;