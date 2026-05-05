import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CarProvider } from './context/CarContext';
import { RentalProvider } from './context/RentalContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import VerifyEmailPage from './pages/VerifyEmailPage';
import CarsPage from './pages/CarsPage';
import CarDetailPage from './pages/CarDetailPage';
import MyRentalsPage from './pages/MyRentalsPage';
import ProfilePage from './pages/ProfilePage';
import AdminCarsPage from './pages/admin/AdminCarsPage';
import AdminRentalsPage from './pages/admin/AdminRentalsPage';
import AdminUsersPage from './pages/admin/AdminUsersPage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CarProvider>
          <RentalProvider>
            <div className="app">
              <Navbar />
              <main className="main-content">
                <Routes>
                  {/* Public */}
                  <Route path="/" element={<Navigate to="/cars" replace />} />
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="/register" element={<RegisterPage />} />
                  <Route path="/verify-email" element={<VerifyEmailPage />} />
                  <Route path="/cars" element={<CarsPage />} />

                  {/* Authenticated */}
                  <Route
                    path="/cars/:id"
                    element={
                      <ProtectedRoute>
                        <CarDetailPage />
                      </ProtectedRoute>
                    }
                  />
                  <Route
                    path="/my-rentals"
                    element={
                      <ProtectedRoute>
                        <MyRentalsPage />
                      </ProtectedRoute>
                    }
                  />
                  <Route
                    path="/profile"
                    element={
                      <ProtectedRoute>
                        <ProfilePage />
                      </ProtectedRoute>
                    }
                  />

                  {/* Admin only */}
                  <Route
                    path="/admin/cars"
                    element={
                      <ProtectedRoute adminOnly>
                        <AdminCarsPage />
                      </ProtectedRoute>
                    }
                  />
                  <Route
                    path="/admin/rentals"
                    element={
                      <ProtectedRoute adminOnly>
                        <AdminRentalsPage />
                      </ProtectedRoute>
                    }
                  />
                  <Route
                    path="/admin/users"
                    element={
                      <ProtectedRoute adminOnly>
                        <AdminUsersPage />
                      </ProtectedRoute>
                    }
                  />

                  {/* Fallback */}
                  <Route path="*" element={<Navigate to="/cars" replace />} />
                </Routes>
              </main>
            </div>
          </RentalProvider>
        </CarProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
