import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useCars } from '../context/CarContext';
import { useRentals } from '../context/RentalContext';
import { useAuth } from '../context/AuthContext';
import Spinner from '../components/Spinner';
import Alert from '../components/Alert';
import StatusBadge from '../components/StatusBadge';

export default function CarDetailPage() {
  const { id } = useParams();
  const { fetchCarById, selectedCar, loading, error } = useCars();
  const { createRental } = useRentals();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ startDate: '', endDate: '' });
  const [bookError, setBookError] = useState('');
  const [bookSuccess, setBookSuccess] = useState('');
  const [booking, setBooking] = useState(false);

  useEffect(() => {
    fetchCarById(id);
  }, [id, fetchCarById]);

  const today = new Date().toISOString().split('T')[0];

  const totalDays =
    form.startDate && form.endDate
      ? Math.max(
          0,
          Math.ceil(
            (new Date(form.endDate) - new Date(form.startDate)) / (1000 * 60 * 60 * 24)
          )
        )
      : 0;

  const totalPrice = selectedCar ? (totalDays * parseFloat(selectedCar.pricePerDay)).toFixed(2) : 0;

  const handleBook = async (e) => {
    e.preventDefault();
    setBookError('');
    setBookSuccess('');

    if (user?.licenseStatus !== 'APPROVED') {
      setBookError('Your driver\'s license must be approved before booking. Please contact support.');
      return;
    }

    setBooking(true);
    try {
      await createRental({ carId: Number(id), startDate: form.startDate, endDate: form.endDate });
      setBookSuccess('Booking successful! Check your rentals for details.');
      setTimeout(() => navigate('/my-rentals'), 2000);
    } catch (err) {
      setBookError(err.response?.data?.message || 'Booking failed. Please try again.');
    } finally {
      setBooking(false);
    }
  };

  if (loading) return <div className="center-content"><Spinner /></div>;
  if (error) return <div className="page"><Alert message={error} /></div>;
  if (!selectedCar) return null;

  const placeholder = 'https://placehold.co/800x400?text=No+Image';

  return (
    <div className="page">
      <button className="btn btn-outline back-btn" onClick={() => navigate(-1)}>
        ← Back
      </button>

      <div className="car-detail">
        <div className="car-detail-image-wrap">
          <img
            src={selectedCar.imageUrl || placeholder}
            alt={`${selectedCar.brand} ${selectedCar.model}`}
            className="car-detail-image"
            onError={(e) => { e.target.src = placeholder; }}
          />
        </div>

        <div className="car-detail-info">
          <div className="car-detail-header">
            <h1>{selectedCar.brand} {selectedCar.model}</h1>
            <StatusBadge status={selectedCar.status} />
          </div>

          <div className="car-detail-specs">
            <div className="spec-item"><span>Year</span><strong>{selectedCar.year}</strong></div>
            <div className="spec-item"><span>Color</span><strong>{selectedCar.color}</strong></div>
            <div className="spec-item"><span>Category</span><strong>{selectedCar.category}</strong></div>
            <div className="spec-item"><span>Fuel</span><strong>{selectedCar.fuelType}</strong></div>
            <div className="spec-item"><span>Seats</span><strong>{selectedCar.seatCount}</strong></div>
            <div className="spec-item"><span>Plate</span><strong>{selectedCar.plateNumber}</strong></div>
          </div>

          <div className="car-detail-price">
            <span className="price-amount">${selectedCar.pricePerDay}</span>
            <span className="price-label"> / day</span>
          </div>

          {selectedCar.status === 'AVAILABLE' ? (
            <div className="booking-form-wrap">
              <h3>Book this car</h3>
              <Alert message={bookError} onClose={() => setBookError('')} />
              <Alert type="success" message={bookSuccess} />
              <form onSubmit={handleBook} className="booking-form">
                <div className="form-row">
                  <div className="form-group">
                    <label>Start Date</label>
                    <input
                      type="date"
                      value={form.startDate}
                      min={today}
                      onChange={(e) => setForm((p) => ({ ...p, startDate: e.target.value }))}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>End Date</label>
                    <input
                      type="date"
                      value={form.endDate}
                      min={form.startDate || today}
                      onChange={(e) => setForm((p) => ({ ...p, endDate: e.target.value }))}
                      required
                    />
                  </div>
                </div>
                {totalDays > 0 && (
                  <div className="booking-summary">
                    <span>{totalDays} day{totalDays > 1 ? 's' : ''}</span>
                    <strong>Total: ${totalPrice}</strong>
                  </div>
                )}
                <button type="submit" className="btn btn-primary btn-full" disabled={booking}>
                  {booking ? 'Booking…' : 'Confirm Booking'}
                </button>
              </form>
            </div>
          ) : (
            <div className="unavailable-notice">
              This car is currently not available for booking.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
