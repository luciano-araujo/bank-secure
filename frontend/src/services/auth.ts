import api from './api';
import type { AuthRequest, AuthResponse } from '../types/auth';

export const authenticate = async (payload: AuthRequest) => (await api.post<AuthResponse>('/auth', payload)).data;

