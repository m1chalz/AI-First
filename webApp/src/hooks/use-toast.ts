import { useState, useRef, useCallback } from 'react';

interface UseToastReturn {
  message: string | null;
  showToast: (message: string, duration?: number) => void;
  clearToast: () => void;
}

export function useToast(): UseToastReturn {
  const [message, setMessage] = useState<string | null>(null);
  const timeoutRef = useRef<number | null>(null);

  const clearToast = useCallback(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
      timeoutRef.current = null;
    }
    setMessage(null);
  }, []);

  const showToast = useCallback((msg: string, duration = 3000) => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }

    setMessage(msg);

    timeoutRef.current = setTimeout(() => {
      setMessage(null);
      timeoutRef.current = null;
    }, duration);
  }, []);

  return {
    message,
    showToast,
    clearToast,
  };
}

