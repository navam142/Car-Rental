import axiosInstance from './axiosInstance';

export const getAvailableCars = (params = {}) =>
  axiosInstance.get('/cars', { params });

export const getAvailableCarsByDateRange = (startDate, endDate) =>
  axiosInstance.get('/cars/available', { params: { startDate, endDate } });

export const getCarById = (id) =>
  axiosInstance.get(`/cars/${id}`);

// Admin
export const createCar = (formData) =>
  axiosInstance.post('/admin/cars', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export const updateCar = (id, data) =>
  axiosInstance.put(`/admin/cars/${id}`, data);

export const deleteCar = (id) =>
  axiosInstance.delete(`/admin/cars/${id}`);

export const updateCarStatus = (id, status) =>
  axiosInstance.patch(`/admin/cars/${id}/status`, { status });
