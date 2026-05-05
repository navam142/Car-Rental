import axiosInstance from './axiosInstance';

export const registerUser = (data) =>
  axiosInstance.post('/auth/register', data);

export const loginUser = (data) =>
  axiosInstance.post('/auth/login', data);

export const verifyEmail = (token) =>
  axiosInstance.get(`/auth/verify-email?token=${token}`);

export const resendVerification = (email) =>
  axiosInstance.post('/auth/resend-verification', { email });

export const getProfile = () =>
  axiosInstance.get('/users/me');

export const updateProfile = (data) =>
  axiosInstance.put('/users/me', data);
