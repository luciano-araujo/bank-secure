import axios from 'axios';

export const getErrorMessage = (error: unknown, fallback = 'Não foi possível completar a operação.') => {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;
    if (typeof data === 'string') {
      return data;
    }
    if (data?.message) {
      return data.message;
    }
  }
  if (error instanceof Error) {
    return error.message;
  }
  return fallback;
};

