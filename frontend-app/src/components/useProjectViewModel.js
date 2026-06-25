// src/components/useProjectViewModel.js
import { useState, useEffect } from 'react';
import { projectService } from '../services/projectService';

export function useProjectViewModel() {
  const [proyectos, setProyectos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // NUEVOS ESTADOS DE NAVEGACIÓN MVVM
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [commits, setCommits] = useState([]);

  const cargarProyectos = () => {
    setLoading(true);
    projectService.getProyectos()
      .then((data) => setProyectos(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    cargarProyectos();
  }, []);

  // Función para seleccionar un proyecto y disparar la carga de sus commits
  const verDetalleProyecto = (id) => {
    setSelectedProjectId(id);
    if (id) {
      projectService.getCommitsByProyecto(id).then((logs) => {
        setCommits(logs);
      });
    } else {
      setCommits([]);
    }
  };

  const total = proyectos.length;
  const completados = proyectos.filter(p => p.estado === 'COMPLETADO').length;
  const enProgreso = proyectos.filter(p => p.estado === 'EN_PROGRESO' || p.estado === 'ACTIVO').length;
  const pendientes = proyectos.filter(p => p.estado === 'PENDIENTE').length;
  const porcentajeAvance = total > 0 ? Math.round((completados / total) * 100) : 0;

  const proyectoSeleccionado = proyectos.find(p => p.id === selectedProjectId);

  return {
    proyectos,
    loading,
    error,
    metrics: { total, completados, enProgreso, pendientes, porcentajeAvance },
    selectedProject: proyectoSeleccionado,
    commits,
    verDetalleProyecto,
    refresh: cargarProyectos
  };
}