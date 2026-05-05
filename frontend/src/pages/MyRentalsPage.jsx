import { useEffect, useState } from 'react';
import { useRentals } from '../context/RentalContext';
import Spinner from '../components/Spinner';
import Alert from '../components/Alert';
import StatusBadge from '../components/StatusBadge';

export default function MyRentalsPage() {
  const { rentals, loading, error, fetchMyRentals, cancel } = useRentals();
  const [cancelError, setCancelError] = useState('');
  const [cancelSuccess, setCancelSuccess] = useState('');
  const [cancellingId, setCancellingId] = useState(null);

  useEffect(() => {
    fetchMyRentals();
  }, [fetchMyRentals]);

  const handleCancel = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this rental?')) return;
    setCancelError('');
    setCancelSuccess('');
    setCancellingId(id);
    try {
      await cancel(id);
      setCancelSuccess('Rental cancelled successfully.');
    } catch (err) {
      setCancelError(err.response?.data?.message || 'Failed to cancel rental.');
    } finally {
      setCancellingId(null);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>My Rentals</h1>
        <p>Track all your car bookings</p>
      </div>

      <Alert message={cancelError} onClose={() => setCancelError('')} />
      <Alert type="success" message={cancelSuccess} onClose={() => setCancelSuccess('')} />
      <Alert message={error} />

      {loading ? (
        <div className="center-content"><Spinner /></div>
      ) : rentals.length === 0 ? (
        <div className="empty-state">
          <p>🚗 You have no rentals yet.</p>
        </div>
      ) : (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Car</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Total</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {rentals.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.carBrand} {r.carModel}<br /><small>{r.carPlateNumber}</small></td>
                  <td>{r.startDate}</td>
                  <td>{r.endDate}</td>
                  <td>${r.totalPrice}</td>
                  <td><StatusBadge status={r.status} /></td>
                  <td>
                    {r.status === 'PENDING' && (
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleCancel(r.id)}
                        disabled={cancellingId === r.id}
                      >
                        {cancellingId === r.id ? 'Cancelling…' : 'Cancel'}
                      </button>
                    )}
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
