import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { initAmplitude, initEntrySource } from './amplitude.js'

const startApp = async () => {
  initEntrySource();

  try {
    await initAmplitude();
  } catch (error) {
    console.error('Amplitude 초기화 실패: ', error);
  }

  createRoot(document.getElementById('root')).render(
    <StrictMode>
      <App />
    </StrictMode>,
  );
};

startApp();