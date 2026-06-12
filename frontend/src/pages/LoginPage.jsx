import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { resendVerification } from '../api/authApi';
import Alert from '../components/Alert';

export default function LoginPage() {
  const { login, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/';

  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [showResend, setShowResend] = useState(false);

  // Resend state
  const [resendStatus, setResendStatus] = useState(''); // '' | 'sending' | 'sent' | 'error'
  const [resendMessage, setResendMessage] = useState('');

  const handleChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setShowResend(false);
    setResendStatus('');
    setResendMessage('');
    try {
      const user = await login(form);
      navigate(user.role === 'ADMIN' ? '/admin/cars' : from, { replace: true });
    } catch (err) {
      const errorCode = err.response?.data?.errorCode;
      setError(err.message);
      if (errorCode === 'EMAIL_NOT_VERIFIED') {
        setShowResend(true);
      }
    }
  };

  const handleResend = async () => {
    setResendStatus('sending');
    setResendMessage('');
    try {
      await resendVerification(form.email);
      setResendStatus('sent');
      setResendMessage('Verification email sent! Check your inbox.');
    } catch (err) {
      setResendStatus('error');
      setResendMessage(err.response?.data?.message || 'Failed to resend. Please try again.');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2 className="auth-title">Welcome back</h2>
        <p className="auth-subtitle">Sign in to your account</p>

        <Alert message={error} onClose={() => { setError(''); setShowResend(false); }} />

        {showResend && resendStatus !== 'sent' && (
          <div className="resend-section">
            <p className="resend-hint">Your email is not verified.</p>
            {resendStatus === 'error' && (
              <Alert message={resendMessage} onClose={() => setResendMessage('')} />
            )}
            <button
              type="button"
              className="btn btn-outline btn-full"
              onClick={handleResend}
              disabled={resendStatus === 'sending'}
            >
              {resendStatus === 'sending' ? 'Sending…' : 'Resend Verification Email'}
            </button>
          </div>
        )}

        {resendStatus === 'sent' && (
          <Alert message={resendMessage} type="success" />
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="you@example.com"
              required
              autoComplete="email"
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="••••••"
              required
              autoComplete="current-password"
            />
          </div>
          <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign In'}
          </button>
        </form>

        <p className="auth-footer">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}
