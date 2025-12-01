import { useEffect } from 'react';

export function useBrowserBackHandler(onBack: () => void): void {
  useEffect(() => {
    window.addEventListener('popstate', onBack);

    return () => {
      window.removeEventListener('popstate', onBack);
    };
  }, [onBack]);
}

