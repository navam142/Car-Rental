import { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { verifyEmail, resendVerification } from '../api/authApi';
import Alert from '../components/Alert';

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState('verifying'); // verifying | success | error
  const [message, setMessage] = useState('');

  // Resend form state
  const [resendEmail, setResendEmail] = useState('');
  const [resendStatus, setResendStatus] = useState(''); // '' | 'sending' | 'sent' | 'error'
  const [resendMessage, setResendMessage] = useState('');

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

  const handleResend = async (e) => {
    e.preventDefault();
    setResendStatus('sending');
    setResendMessage('');
    try {
      await resendVerification(resendEmail);
      setResendStatus('sent');
      setResendMessage('Verification email sent! Check your inbox.');
    } catch (err) {
      setResendStatus('error');
      setResendMessage(err.response?.data?.message || 'Failed to resend. Please try again.');
    }
  };

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

            <div className="resend-section">
              <p className="resend-hint">Need a new verification link?</p>
              {resendStatus === 'sent' ? (
                <Alert message={resendMessage} type="success" />
              ) : (
                <form onSubmit={handleResend} className="resend-form">
                  <Alert message={resendStatus === 'error' ? resendMessage : ''} onClose={() => setResendMessage('')} />
                  <div className="form-group">
                    <input
                      type="email"
                      value={resendEmail}
                      onChange={(e) => setResendEmail(e.target.value)}
                      placeholder="Enter your email"
                      required
                      autoComplete="email"
                    />
                  </div>
                  <button
                    type="submit"
                    className="btn btn-primary btn-full"
                    disabled={resendStatus === 'sending'}
                  >
                    {resendStatus === 'sending' ? 'Sending…' : 'Resend Verification Email'}
                  </button>
                </form>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}
