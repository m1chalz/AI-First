import React from 'react';
import ReactDOM from 'react-dom/client';
import { AnimalList } from './components/AnimalList/AnimalList.tsx';
import { initializeKoin } from './di/koinSetup';

// Initialize Koin dependency injection before rendering
initializeKoin();

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

/**
 * Main application entry point.
 * Sets AnimalList as the primary screen per FR-010.
 */
ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    <AnimalList />
  </React.StrictMode>
);