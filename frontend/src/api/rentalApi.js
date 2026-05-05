import axiosInstance from './axiosInstance';

export const bookCar = (data) =>
  axiosInstance.post('/rentals', data);

export const getMyRentals = () =>
  axiosInstance.get('/rentals/my');

export const cancelRental = (id) =>
  axiosInstance.delete(`/rentals/${id}`);

// Admin
export const getAllRentals = () =>
  axiosInstance.get('/admin/rentals');

export const updateRentalStatus = (id, status) =>
  axiosInstance.patch(`/admin/rentals/${id}/status`, { status });
