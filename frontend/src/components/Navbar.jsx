import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout, isAdmin, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">🚗 CarRental</Link>
      </div>
      <div className="navbar-links">
        <Link to="/cars">Browse Cars</Link>
        {isAuthenticated && !isAdmin && (
          <>
            <Link to="/my-rentals">My Rentals</Link>
            <Link to="/profile">Profile</Link>
          </>
        )}
        {isAdmin && (
          <>
            <Link to="/admin/cars">Manage Cars</Link>
            <Link to="/admin/rentals">Manage Rentals</Link>
            <Link to="/admin/users">Manage Users</Link>
          </>
        )}
        {isAuthenticated ? (
          <div className="navbar-user">
            <span className="navbar-username">Hi, {user.name}</span>
            <button className="btn btn-outline" onClick={handleLogout}>
              Logout
            </button>
          </div>
        ) : (
          <div className="navbar-auth">
            <Link to="/login" className="btn btn-outline">Login</Link>
            <Link to="/register" className="btn btn-primary">Register</Link>
          </div>
        )}
      </div>
    </nav>
  );
}
