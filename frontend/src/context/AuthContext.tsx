import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import type { AuthRequest, AuthResponse } from '../types/auth';
import { authenticate } from '../services/auth';
import { getErrorMessage } from '../utils/errorMessage';

type AuthContextValue = {
  user: AuthResponse | null;
  login: (payload: AuthRequest) => Promise<void>;
  logout: () => void;
  isLoading: boolean;
  error: string | null;
  clearError: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);
const STORAGE_KEY = 'banksecure:user';

const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<AuthResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      try {
        const parsed: AuthResponse = JSON.parse(stored);
        setUser(parsed);
      } catch {
        localStorage.removeItem(STORAGE_KEY);
      }
    }
    setIsLoading(false);
  }, []);

  const login = useCallback(async (payload: AuthRequest) => {
    setError(null);
    const response = await authenticate(payload);
    if (!response.authenticated) {
      throw new Error('Credenciais inválidas');
    }
    if (response.tipoUsuario !== 'FUNCIONARIO') {
      throw new Error('Somente funcionários podem acessar o portal.');
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(response));
    setUser(response);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    setUser(null);
  }, []);

  const safeLogin = useCallback(
    async (payload: AuthRequest) => {
      try {
        await login(payload);
      } catch (errorCause) {
        setError(getErrorMessage(errorCause));
        throw errorCause;
      }
    },
    [login],
  );

  const value = useMemo(
    () => ({
      user,
      login: safeLogin,
      logout,
      isLoading,
      error,
      clearError: () => setError(null),
    }),
    [user, safeLogin, logout, isLoading, error],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider');
  }
  return context;
};

export default AuthProvider;

