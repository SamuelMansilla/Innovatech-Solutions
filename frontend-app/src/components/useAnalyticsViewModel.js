// src/components/useAnalyticsViewModel.js
import { useState, useEffect } from 'react';
import { analyticsService } from '../services/analyticsService';

export function useAnalyticsViewModel() {
  const [sector, setSector] = useState('fintech');
  const [reporte, setReporte] = useState(null);
  const [loading, setLoading] = useState(false);
  const [circuitStatus, setCircuitStatus] = useState('CLOSED'); // CLOSED, OPEN, HALF-OPEN

  const consultarReporte = (sectorActivo) => {
    setLoading(true);
    analyticsService.getReporteSector(sectorActivo)
      .then((res) => {
        setReporte(res);
        // Si el backend o el fallback nos dice "degraded", el circuito está ABIERTO
        if (res.status === 'degraded' || res.status === 503) {
          setCircuitStatus('OPEN');
        } else {
          setCircuitStatus('CLOSED');
        }
      })
      .catch(() => {
        setCircuitStatus('OPEN');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  // Cada vez que la directiva cambie de sector, ejecutamos el Factory Method
  useEffect(() => {
    consultarReporte(sector);
  }, [sector]);

  return {
    sector,
    setSector,
    reporte,
    loading,
    circuitStatus,
    triggerRetry: () => consultarReporte(sector)
  };
}