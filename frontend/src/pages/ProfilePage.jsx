import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { updateProfile } from '../api/authApi';
import Alert from '../components/Alert';
import StatusBadge from '../components/StatusBadge';

export default function ProfilePage() {
  const { user, refreshProfile } = useAuth();
  const [form, setForm] = useState({
    name: user?.name || '',
    email: user?.email || '',
    password: '',
    licenseNo: user?.licenseNo || '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    console.log(user);
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      const payload = { ...form };
      
      if (!payload.password) delete payload.password;
      await updateProfile(payload);
      await refreshProfile();
      setSuccess('Profile updated successfully.');
      setForm((p) => ({ ...p, password: '' }));
    } catch (err) {
      setError(err.response?.data?.message || 'Update failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>My Profile</h1>
      </div>

      <div className="profile-layout">
        {/* Info card */}
        <div className="profile-card">
          <div className="profile-avatar">{user?.name?.[0]?.toUpperCase()}</div>
          <h3>{user?.name}</h3>
          <p className="profile-email">{user?.email}</p>
          <div className="profile-badges">
            <span className="badge badge-info">{user?.role}</span>
            <StatusBadge status={user?.licenseStatus} />
          </div>
          <div className="profile-meta">
            <span>License: {user?.licenseNo}</span>
            <span>Email verified: {user?.verified ? '✅' : '❌'}</span>
          </div>
        </div>

        {/* Edit form */}
        <div className="profile-form-wrap">
          <h3>Edit Profile</h3>
          <Alert message={error} onClose={() => setError('')} />
          <Alert type="success" message={success} onClose={() => setSuccess('')} />

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="name">Full Name</label>
              <input
                id="name"
                type="text"
                name="name"
                value={form.name}
                onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="password">New Password <small>(leave blank to keep current)</small></label>
              <input
                id="password"
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="Min. 6 characters"
                minLength={form.password ? 6 : undefined}
                autoComplete="new-password"
              />
            </div>
            <div className="form-group">
              <label htmlFor="licenseNo">License Number</label>
              <input
                id="licenseNo"
                type="text"
                name="licenseNo"
                value={form.licenseNo}
                onChange={handleChange}
              />
            </div>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving…' : 'Save Changes'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
