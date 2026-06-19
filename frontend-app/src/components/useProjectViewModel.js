// src/components/useProjectViewModel.js
import { useState, useEffect } from 'react';
import { projectService } from '../services/projectService';

export function useProjectViewModel() {
  const [proyectos, setProyectos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const cargarProyectos = () => {
    setLoading(true);
    projectService.getProyectos()
      .then((data) => {
        setProyectos(data);
        setError(null);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    cargarProyectos();
    const interval = setInterval(cargarProyectos, 15000); // Polling cada 15s (Observer pattern simulado)
    return () => clearInterval(interval);
  }, []);

  // LÓGICA CORE DE MVVM: Transformar el estado crudo en métricas de avance
  const total = proyectos.length;
  const completados = proyectos.filter(p => p.estado === 'COMPLETADO').length;
  const enProgreso = proyectos.filter(p => p.estado === 'EN_PROGRESO' || p.estado === 'ACTIVO').length;
  const pendientes = proyectos.filter(p => p.estado === 'PENDIENTE').length;
  
  // Porcentaje de progreso global de la compañía
  const porcentajeAvance = total > 0 ? Math.round((completados / total) * 100) : 0;

  return {
    proyectos,
    loading,
    error,
    metrics: { total, completados, enProgreso, pendientes, porcentajeAvance },
    refresh: cargarProyectos
  };
}