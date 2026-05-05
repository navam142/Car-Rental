import { createContext, useContext, useState, useCallback } from 'react';
import {
  getAvailableCars,
  getAvailableCarsByDateRange,
  getCarById,
  createCar,
  updateCar,
  deleteCar,
  updateCarStatus,
} from '../api/carApi';

const CarContext = createContext(null);

export function CarProvider({ children }) {
  const [cars, setCars] = useState([]);
  const [selectedCar, setSelectedCar] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchCars = useCallback(async (filters = {}) => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await getAvailableCars(filters);
      setCars(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch cars');
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchCarsByDateRange = useCallback(async (startDate, endDate) => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await getAvailableCarsByDateRange(startDate, endDate);
      setCars(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch cars');
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchCarById = useCallback(async (id) => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await getCarById(id);
      setSelectedCar(data);
      return data;
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch car');
    } finally {
      setLoading(false);
    }
  }, []);

  const addCar = useCallback(async (formData) => {
    const { data } = await createCar(formData);
    setCars((prev) => [...prev, data]);
    return data;
  }, []);

  const editCar = useCallback(async (id, carData) => {
    const { data } = await updateCar(id, carData);
    setCars((prev) => prev.map((c) => (c.id === id ? data : c)));
    return data;
  }, []);

  const removeCar = useCallback(async (id) => {
    await deleteCar(id);
    setCars((prev) => prev.filter((c) => c.id !== id));
  }, []);

  const changeCarStatus = useCallback(async (id, status) => {
    const { data } = await updateCarStatus(id, status);
    setCars((prev) => prev.map((c) => (c.id === id ? data : c)));
    return data;
  }, []);

  return (
    <CarContext.Provider
      value={{
        cars,
        selectedCar,
        loading,
        error,
        fetchCars,
        fetchCarsByDateRange,
        fetchCarById,
        addCar,
        editCar,
        removeCar,
        changeCarStatus,
      }}
    >
      {children}
    </CarContext.Provider>
  );
}

export function useCars() {
  const ctx = useContext(CarContext);
  if (!ctx) throw new Error('useCars must be used within CarProvider');
  return ctx;
}
