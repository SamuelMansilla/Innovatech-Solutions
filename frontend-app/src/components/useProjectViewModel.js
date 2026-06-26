// src/components/useProjectViewModel.js
import { useState, useEffect } from 'react';
import { projectService } from '../services/projectService';

export function useProjectViewModel() {
  const [proyectos, setProyectos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
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

  const verDetalleProyecto = (id) => {
    setSelectedProjectId(id);
    if (id) {
      projectService.getCommitsByProyecto(id).then((logs) => setCommits(logs));
    } else {
      setCommits([]);
    }
  };

  // 🔥 LÓGICA INTELIGENTE: Si el nombre coincide, edita. Si no existe, crea.
  const agregarProyecto = async (formData) => {
    setLoading(true);
    setError(null);
    try {
      const proyectosSeguros = Array.isArray(proyectos) ? proyectos : [];
      
      // Buscamos si ya existe una tarea/proyecto con el mismo nombre (sin importar mayúsculas)
      const proyectoExistente = proyectosSeguros.find(
        p => p.nombre.toLowerCase().trim() === formData.nombre.toLowerCase().trim()
      );

      if (proyectoExistente) {
        // Mismo nombre detectado: Seguimos en la misma tarea e inyectamos el cambio
        await projectService.actualizarProyecto(proyectoExistente.id, formData);
        alert('¡El proyecto ya existía! Se ha registrado el nuevo cambio en el historial de la misma tarea.');
      } else {
        // Nombre nuevo: Creamos el registro inicial
        await projectService.crearProyecto(formData);
      }

      cargarProyectos(); // Recarga la lista desde base de datos
      return { success: true };
    } catch (err) {
      setError(err.message);
      setLoading(false);
      return { success: false };
    }
  };

  const proyectosSeguros = Array.isArray(proyectos) ? proyectos : [];

  const total = proyectosSeguros.length;
  const completados = proyectosSeguros.filter(p => p.estado === 'COMPLETADO').length;
  const enProgreso = proyectosSeguros.filter(p => p.estado === 'EN_PROGRESO' || p.estado === 'ACTIVO').length;
  const pendientes = proyectosSeguros.filter(p => p.estado === 'PENDIENTE' || p.estado === 'PLANIFICADO').length;
  const porcentajeAvance = total > 0 ? Math.round((completados / total) * 100) : 0;

  const proyectoSeleccionado = proyectosSeguros.find(p => p.id === selectedProjectId);

  return {
    proyectos: proyectosSeguros,
    loading,
    error,
    metrics: { total, completados, enProgreso, pendientes, porcentajeAvance },
    selectedProject: proyectoSeleccionado,
    commits,
    verDetalleProyecto,
    refresh: cargarProyectos,
    agregarProyecto
  };
}