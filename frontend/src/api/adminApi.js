import axiosInstance from './axiosInstance';

export const getAllUsers = () =>
  axiosInstance.get('/admin/users');

export const updateLicenseStatus = (id, licenseStatus) =>
  axiosInstance.put(`/admin/users/${id}/license`, { licenseStatus });
