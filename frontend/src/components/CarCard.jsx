import { Link } from 'react-router-dom';

export default function CarCard({ car, onBook }) {
  const placeholder = 'https://placehold.co/400x220?text=No+Image';

  return (
    <div className="car-card">
      <img
        src={car.imageUrl || placeholder}
        alt={`${car.brand} ${car.model}`}
        className="car-card-image"
        onError={(e) => { e.target.src = placeholder; }}
      />
      <div className="car-card-body">
        <h3 className="car-card-title">{car.brand} {car.model}</h3>
        <div className="car-card-meta">
          <span className="badge badge-category">{car.category}</span>
          <span className="badge badge-fuel">{car.fuelType}</span>
        </div>
        <div className="car-card-details">
          <span>📅 {car.year}</span>
          <span>💺 {car.seatCount} seats</span>
        </div>
        <div className="car-card-price">
          <strong>${car.pricePerDay}</strong>
          <span> / day</span>
        </div>
        <div className="car-card-actions">
          {onBook ? (
            <button className="btn btn-primary" onClick={() => onBook(car)}>
              Book Now
            </button>
          ) : (
            <Link to={`/cars/${car.id}`} className="btn btn-primary">
              View Details
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}
