import { createContext, useContext, useState, useCallback } from 'react';
import {
  bookCar,
  getMyRentals,
  cancelRental,
  getAllRentals,
  updateRentalStatus,
} from '../api/rentalApi';

const RentalContext = createContext(null);

export function RentalProvider({ children }) {
  const [rentals, setRentals] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchMyRentals = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await getMyRentals();
      setRentals(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch rentals');
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchAllRentals = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await getAllRentals();
      setRentals(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch rentals');
    } finally {
      setLoading(false);
    }
  }, []);

  const createRental = useCallback(async (rentalData) => {
    const { data } = await bookCar(rentalData);
    setRentals((prev) => [data, ...prev]);
    return data;
  }, []);

  const cancel = useCallback(async (id) => {
    const { data } = await cancelRental(id);
    setRentals((prev) => prev.map((r) => (r.id === id ? data : r)));
    return data;
  }, []);

  const changeRentalStatus = useCallback(async (id, status) => {
    const { data } = await updateRentalStatus(id, status);
    setRentals((prev) => prev.map((r) => (r.id === id ? data : r)));
    return data;
  }, []);

  return (
    <RentalContext.Provider
      value={{
        rentals,
        loading,
        error,
        fetchMyRentals,
        fetchAllRentals,
        createRental,
        cancel,
        changeRentalStatus,
      }}
    >
      {children}
    </RentalContext.Provider>
  );
}

export function useRentals() {
  const ctx = useContext(RentalContext);
  if (!ctx) throw new Error('useRentals must be used within RentalProvider');
  return ctx;
}
