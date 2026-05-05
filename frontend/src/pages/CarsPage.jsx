import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCars } from '../context/CarContext';
import { useAuth } from '../context/AuthContext';
import CarCard from '../components/CarCard';
import Spinner from '../components/Spinner';
import Alert from '../components/Alert';

const CATEGORIES = ['', 'SEDAN', 'SUV', 'HATCHBACK', 'CONVERTIBLE', 'TRUCK', 'VAN', 'COUPE'];
const FUEL_TYPES = ['', 'PETROL', 'DIESEL', 'ELECTRIC', 'HYBRID'];

export default function CarsPage() {
  const { cars, loading, error, fetchCars, fetchCarsByDateRange } = useCars();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [filters, setFilters] = useState({ category: '', fuelType: '' });
  const [dateRange, setDateRange] = useState({ startDate: '', endDate: '' });
  const [dateMode, setDateMode] = useState(false);

  useEffect(() => {
    fetchCars();
  }, [fetchCars]);

  const handleFilterChange = (e) => {
    const updated = { ...filters, [e.target.name]: e.target.value };
    setFilters(updated);
    const params = {};
    if (updated.category) params.category = updated.category;
    if (updated.fuelType) params.fuelType = updated.fuelType;
    fetchCars(params);
  };

  const handleDateSearch = (e) => {
    e.preventDefault();
    if (dateRange.startDate && dateRange.endDate) {
      fetchCarsByDateRange(dateRange.startDate, dateRange.endDate);
    }
  };

  const handleBook = (car) => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    navigate(`/cars/${car.id}`);
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Available Cars</h1>
        <p>Find the perfect car for your trip</p>
      </div>

      {/* Filter bar */}
      <div className="filter-bar">
        <div className="filter-tabs">
          <button
            className={`tab ${!dateMode ? 'active' : ''}`}
            onClick={() => { setDateMode(false); fetchCars(); }}
          >
            Browse All
          </button>
          <button
            className={`tab ${dateMode ? 'active' : ''}`}
            onClick={() => setDateMode(true)}
          >
            Search by Dates
          </button>
        </div>

        {!dateMode ? (
          <div className="filter-controls">
            <select name="category" value={filters.category} onChange={handleFilterChange}>
              {CATEGORIES.map((c) => (
                <option key={c} value={c}>{c || 'All Categories'}</option>
              ))}
            </select>
            <select name="fuelType" value={filters.fuelType} onChange={handleFilterChange}>
              {FUEL_TYPES.map((f) => (
                <option key={f} value={f}>{f || 'All Fuel Types'}</option>
              ))}
            </select>
          </div>
        ) : (
          <form className="date-filter" onSubmit={handleDateSearch}>
            <div className="form-group">
              <label>Start Date</label>
              <input
                type="date"
                value={dateRange.startDate}
                min={new Date().toISOString().split('T')[0]}
                onChange={(e) => setDateRange((p) => ({ ...p, startDate: e.target.value }))}
                required
              />
            </div>
            <div className="form-group">
              <label>End Date</label>
              <input
                type="date"
                value={dateRange.endDate}
                min={dateRange.startDate || new Date().toISOString().split('T')[0]}
                onChange={(e) => setDateRange((p) => ({ ...p, endDate: e.target.value }))}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary">Search</button>
          </form>
        )}
      </div>

      <Alert message={error} />

      {loading ? (
        <div className="center-content"><Spinner /></div>
      ) : cars.length === 0 ? (
        <div className="empty-state">
          <p>🚗 No cars available matching your criteria.</p>
        </div>
      ) : (
        <div className="car-grid">
          {cars.map((car) => (
            <CarCard key={car.id} car={car} onBook={handleBook} />
          ))}
        </div>
      )}
    </div>
  );
}
