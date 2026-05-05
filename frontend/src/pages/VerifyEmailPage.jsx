import { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { verifyEmail } from '../api/authApi';

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState('verifying'); // verifying | success | error
  const [message, setMessage] = useState('');

  useEffect(() => {
    const token = searchParams.get('token');
    if (!token) {
      setStatus('error');
      setMessage('No verification token found.');
      return;
    }
    verifyEmail(token)
      .then(() => {
        setStatus('success');
        setMessage('Your email has been verified! You can now log in.');
      })
      .catch((err) => {
        setStatus('error');
        setMessage(err.response?.data?.message || 'Verification failed. The link may have expired.');
      });
  }, [searchParams]);

  return (
    <div className="auth-page">
      <div className="auth-card text-center">
        {status === 'verifying' && (
          <>
            <div className="verify-icon">⏳</div>
            <h2>Verifying your email…</h2>
          </>
        )}
        {status === 'success' && (
          <>
            <div className="verify-icon success">✅</div>
            <h2>Email Verified!</h2>
            <p>{message}</p>
            <Link to="/login" className="btn btn-primary">Go to Login</Link>
          </>
        )}
        {status === 'error' && (
          <>
            <div className="verify-icon error">❌</div>
            <h2>Verification Failed</h2>
            <p>{message}</p>
            <Link to="/login" className="btn btn-outline">Back to Login</Link>
          </>
        )}
      </div>
    </div>
  );
}
