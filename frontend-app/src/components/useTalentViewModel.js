// src/components/useTalentViewModel.js
import { useState, useEffect } from 'react';
import { talentService } from "../services/talentServices.js";

export function useTalentViewModel() {
  const [empleados, setEmpleados] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // El patrón Observer se apoya aquí en el ciclo de vida del componente.
  // Al actualizar el estado interno, notificará automáticamente a las Vistas ligadas.
  const cargarTalento = () => {
    setLoading(true);
    talentService.getProfesionales()
      .then((data) => {
        setEmpleados(data);
        setError(null);
      })
      .catch((err) => {
        setError(err.message || 'Error desconocido de red');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    cargarTalento();
    
    // Simulación de sondeo (polling) para cumplir el requerimiento de "Tiempo Real" 
    // del patrón Observer si no se implementa un WebSocket completo.
    const interval = setInterval(cargarTalento, 10000); // Actualiza cada 10 segundos
    
    return () => clearInterval(interval);
  }, []);

  return {
    empleados,
    loading,
    error,
    refresh: cargarTalento
  };
}