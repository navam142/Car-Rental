import { useState } from 'react';
import { useCars } from '../../context/CarContext';
import Alert from '../../components/Alert';

const CATEGORIES = ['SEDAN', 'SUV', 'HATCHBACK', 'CONVERTIBLE', 'TRUCK', 'VAN', 'COUPE'];
const FUEL_TYPES = ['PETROL', 'DIESEL', 'ELECTRIC', 'HYBRID'];

const emptyForm = {
  brand: '',
  model: '',
  year: '',
  color: '',
  plateNumber: '',
  seatCount: '',
  pricePerDay: '',
  fuelType: 'PETROL',
  category: 'SEDAN',
};

export default function CarFormModal({ car, onClose, onSaved }) {
  const { addCar, editCar } = useCars();
  const isEdit = !!car;

  const [form, setForm] = useState(
    isEdit
      ? {
          brand: car.brand,
          model: car.model,
          year: car.year,
          color: car.color,
          plateNumber: car.plateNumber,
          seatCount: car.seatCount,
          pricePerDay: car.pricePerDay,
          fuelType: car.fuelType,
          category: car.category,
        }
      : emptyForm
  );
  const [image, setImage] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      if (isEdit) {
        await editCar(car.id, form);
      } else {
        if (!image) {
          setError('Please select a car image.');
          setLoading(false);
          return;
        }
        const formData = new FormData();
        // Backend expects multipart: "car" part as JSON blob + "image" part
        formData.append('car', new Blob([JSON.stringify(form)], { type: 'application/json' }));
        formData.append('image', image);
        await addCar(formData);
      }
      onSaved();
    } catch (err) {
      setError(err.response?.data?.message || 'Save failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{isEdit ? 'Edit Car' : 'Add New Car'}</h2>
          <button className="modal-close" onClick={onClose} aria-label="Close">×</button>
        </div>

        <Alert message={error} onClose={() => setError('')} />

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-row">
            <div className="form-group">
              <label>Brand</label>
              <input name="brand" value={form.brand} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Model</label>
              <input name="model" value={form.model} onChange={handleChange} required />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Year</label>
              <input type="number" name="year" value={form.year} onChange={handleChange} min="1990" max="2100" required />
            </div>
            <div className="form-group">
              <label>Color</label>
              <input name="color" value={form.color} onChange={handleChange} required />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Plate Number</label>
              <input name="plateNumber" value={form.plateNumber} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Seat Count</label>
              <input type="number" name="seatCount" value={form.seatCount} onChange={handleChange} min="1" max="20" required />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Price Per Day ($)</label>
              <input type="number" name="pricePerDay" value={form.pricePerDay} onChange={handleChange} min="0.01" step="0.01" required />
            </div>
            <div className="form-group">
              <label>Category</label>
              <select name="category" value={form.category} onChange={handleChange}>
                {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Fuel Type</label>
              <select name="fuelType" value={form.fuelType} onChange={handleChange}>
                {FUEL_TYPES.map((f) => <option key={f} value={f}>{f}</option>)}
              </select>
            </div>
            {!isEdit && (
              <div className="form-group">
                <label>Car Image</label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => setImage(e.target.files[0])}
                  required
                />
              </div>
            )}
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving…' : isEdit ? 'Update Car' : 'Create Car'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
