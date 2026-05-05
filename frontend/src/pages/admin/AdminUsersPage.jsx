import { useEffect, useState } from 'react';
import { getAllUsers, updateLicenseStatus } from '../../api/adminApi';
import Spinner from '../../components/Spinner';
import Alert from '../../components/Alert';
import StatusBadge from '../../components/StatusBadge';

export default function AdminUsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [actionError, setActionError] = useState('');
  const [actionSuccess, setActionSuccess] = useState('');

  useEffect(() => {
    setLoading(true);
    getAllUsers()
      .then(({ data }) => setUsers(data))
      .catch((err) => setError(err.response?.data?.message || 'Failed to load users.'))
      .finally(() => setLoading(false));
  }, []);

  const handleLicenseChange = async (id, licenseStatus) => {
    setActionError('');
    try {
      const { data } = await updateLicenseStatus(id, licenseStatus);
      setUsers((prev) => prev.map((u) => (u.id === id ? data : u)));
      setActionSuccess(`License status updated to ${licenseStatus}.`);
    } catch (err) {
      setActionError(err.response?.data?.message || 'Update failed.');
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Manage Users</h1>
      </div>

      <Alert message={actionError} onClose={() => setActionError('')} />
      <Alert type="success" message={actionSuccess} onClose={() => setActionSuccess('')} />
      <Alert message={error} />

      {loading ? (
        <div className="center-content"><Spinner /></div>
      ) : (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Verified</th>
                <th>License No.</th>
                <th>License Status</th>
                <th>Update License</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.name}</td>
                  <td>{u.email}</td>
                  <td><span className="badge badge-info">{u.role}</span></td>
                  <td>{u.isVerified ? '✅' : '❌'}</td>
                  <td>{u.licenseNo}</td>
                  <td><StatusBadge status={u.licenseStatus} /></td>
                  <td>
                    <select
                      value={u.licenseStatus}
                      onChange={(e) => handleLicenseChange(u.id, e.target.value)}
                      className="status-select"
                    >
                      {['PENDING', 'APPROVED', 'REJECTED'].map((s) => (
                        <option key={s} value={s}>{s}</option>
                      ))}
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
