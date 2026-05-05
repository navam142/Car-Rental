import { useEffect, useState } from 'react';
import { useCars } from '../../context/CarContext';
import Spinner from '../../components/Spinner';
import Alert from '../../components/Alert';
import StatusBadge from '../../components/StatusBadge';
import CarFormModal from './CarFormModal';

export default function AdminCarsPage() {
  const { cars, loading, error, fetchCars, removeCar, changeCarStatus } = useCars();
  const [showModal, setShowModal] = useState(false);
  const [editingCar, setEditingCar] = useState(null);
  const [actionError, setActionError] = useState('');
  const [actionSuccess, setActionSuccess] = useState('');

  useEffect(() => {
    // Admin fetches all cars — reuse fetchCars (no filter = all available)
    // For a full admin view we call without filters
    fetchCars();
  }, [fetchCars]);

  const handleEdit = (car) => {
    setEditingCar(car);
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this car? This cannot be undone.')) return;
    setActionError('');
    try {
      await removeCar(id);
      setActionSuccess('Car deleted.');
    } catch (err) {
      setActionError(err.response?.data?.message || 'Delete failed.');
    }
  };

  const handleStatusChange = async (id, status) => {
    setActionError('');
    try {
      await changeCarStatus(id, status);
      setActionSuccess(`Status updated to ${status}.`);
    } catch (err) {
      setActionError(err.response?.data?.message || 'Status update failed.');
    }
  };

  const handleModalClose = () => {
    setShowModal(false);
    setEditingCar(null);
  };

  const handleSaved = () => {
    handleModalClose();
    fetchCars();
    setActionSuccess(editingCar ? 'Car updated.' : 'Car created.');
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Manage Cars</h1>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          + Add Car
        </button>
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
                <th>Image</th>
                <th>Car</th>
                <th>Plate</th>
                <th>Category</th>
                <th>Fuel</th>
                <th>Price/Day</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {cars.map((car) => (
                <tr key={car.id}>
                  <td>{car.id}</td>
                  <td>
                    <img
                      src={car.imageUrl || 'https://placehold.co/60x40?text=N/A'}
                      alt={car.brand}
                      className="table-thumb"
                      onError={(e) => { e.target.src = 'https://placehold.co/60x40?text=N/A'; }}
                    />
                  </td>
                  <td>{car.brand} {car.model} ({car.year})</td>
                  <td>{car.plateNumber}</td>
                  <td>{car.category}</td>
                  <td>{car.fuelType}</td>
                  <td>${car.pricePerDay}</td>
                  <td>
                    <select
                      value={car.status}
                      onChange={(e) => handleStatusChange(car.id, e.target.value)}
                      className="status-select"
                    >
                      {['AVAILABLE', 'RENTED', 'MAINTENANCE'].map((s) => (
                        <option key={s} value={s}>{s}</option>
                      ))}
                    </select>
                  </td>
                  <td className="action-cell">
                    <button className="btn btn-outline btn-sm" onClick={() => handleEdit(car)}>
                      Edit
                    </button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(car.id)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showModal && (
        <CarFormModal
          car={editingCar}
          onClose={handleModalClose}
          onSaved={handleSaved}
        />
      )}
    </div>
  );
}
