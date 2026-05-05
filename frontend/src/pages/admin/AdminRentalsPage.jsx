import { useEffect, useState } from 'react';
import { useRentals } from '../../context/RentalContext';
import Spinner from '../../components/Spinner';
import Alert from '../../components/Alert';
import StatusBadge from '../../components/StatusBadge';

const RENTAL_STATUSES = ['PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED'];

export default function AdminRentalsPage() {
  const { rentals, loading, error, fetchAllRentals, changeRentalStatus } = useRentals();
  const [actionError, setActionError] = useState('');
  const [actionSuccess, setActionSuccess] = useState('');
  const [filter, setFilter] = useState('');

  useEffect(() => {
    fetchAllRentals();
  }, [fetchAllRentals]);

  const handleStatusChange = async (id, status) => {
    setActionError('');
    try {
      await changeRentalStatus(id, status);
      setActionSuccess(`Rental #${id} status updated to ${status}.`);
    } catch (err) {
      setActionError(err.response?.data?.message || 'Status update failed.');
    }
  };

  const filtered = filter ? rentals.filter((r) => r.status === filter) : rentals;

  return (
    <div className="page">
      <div className="page-header">
        <h1>Manage Rentals</h1>
        <select value={filter} onChange={(e) => setFilter(e.target.value)} className="filter-select">
          <option value="">All Statuses</option>
          {RENTAL_STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
        </select>
      </div>

      <Alert message={actionError} onClose={() => setActionError('')} />
      <Alert type="success" message={actionSuccess} onClose={() => setActionSuccess('')} />
      <Alert message={error} />

      {loading ? (
        <div className="center-content"><Spinner /></div>
      ) : filtered.length === 0 ? (
        <div className="empty-state"><p>No rentals found.</p></div>
      ) : (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>#</th>
                <th>User</th>
                <th>Car</th>
                <th>Start</th>
                <th>End</th>
                <th>Total</th>
                <th>Status</th>
                <th>Update Status</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.userName}<br /><small>ID: {r.userId}</small></td>
                  <td>{r.carBrand} {r.carModel}<br /><small>{r.carPlateNumber}</small></td>
                  <td>{r.startDate}</td>
                  <td>{r.endDate}</td>
                  <td>${r.totalPrice}</td>
                  <td><StatusBadge status={r.status} /></td>
                  <td>
                    <select
                      value={r.status}
                      onChange={(e) => handleStatusChange(r.id, e.target.value)}
                      className="status-select"
                    >
                      {RENTAL_STATUSES.map((s) => (
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
