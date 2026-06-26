// src/services/projectService.js

const API_BASE_URL = 'http://localhost:8083/api/v1/proyectos';

// Inicializamos el historial de cambios de forma dinámica en el navegador
if (!localStorage.getItem('DYNAMIC_COMMITS_LOGS')) {
  const semillaInicial = {
    1: [
      { id: 'c1', autor: 'Samuel Mansilla', mensaje: 'feat: estructurar el patrón Repository para la persistencia core', fecha: '2026-06-15 14:30' },
      { id: 'c2', autor: 'Hans Gómez', mensaje: 'fix: corregir asignación de hilos en el Circuit Breaker de Analítica', fecha: '2026-06-16 09:15' }
    ],
    2: [
      { id: 'c4', autor: 'Francisco Mardones', mensaje: 'feat: inicializar modelos de auditoría reguladora gubernamental', fecha: '2026-06-10 16:45' }
    ],
    3: [
      { id: 'c5', autor: 'Hans Gómez', mensaje: 'feat: implementar Factory Method para reportes dinámicos de Retail', fecha: '2026-06-05 18:22' }
    ]
  };
  localStorage.setItem('DYNAMIC_COMMITS_LOGS', JSON.stringify(semillaInicial));
}

export const projectService = {
  getProyectos: async () => {
    try {
      const response = await fetch(API_BASE_URL);
      if (!response.ok) throw new Error('Sin respuesta del servidor de proyectos.');
      const rawData = await response.json();
      return rawData.content ? rawData.content : rawData;
    } catch (error) {
      return [];
    }
  },

  // Lee el historial dinámico según el ID real que tenga el proyecto
  getCommitsByProyecto: async (projectId) => {
    const logs = JSON.parse(localStorage.getItem('DYNAMIC_COMMITS_LOGS')) || {};
    return logs[projectId] || [];
  },

  // Crea un nuevo proyecto e inicializa su bitácora de cambios
  crearProyecto: async (proyectoData) => {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(proyectoData)
    });
    if (!response.ok) throw new Error('Error al crear el proyecto');
    const nuevoProyecto = await response.json();

    // Escribe el primer cambio en el historial automáticamente
    projectService.registrarCambioLocal(nuevoProyecto.id, {
      autor: 'Samuel Mansilla',
      mensaje: `🚀 Proyecto inicializado bajo estado operativo: ${nuevoProyecto.estado}`
    });

    return nuevoProyecto;
  },

  // Modifica el proyecto existente en el Backend (Spring Boot)
  actualizarProyecto: async (id, proyectoData) => {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(proyectoData)
    });
    if (!response.ok) throw new Error('Error al actualizar el proyecto');
    const proyectoActualizado = await response.json();

    // Escribe la modificación en el historial para que sigan la misma tarea
    projectService.registrarCambioLocal(id, {
      autor: 'Samuel Mansilla',
      mensaje: `📝 Cambio registrado: Modificación de alcance / Estado cambiado a [${proyectoActualizado.estado}]`
    });

    return proyectoActualizado;
  },

  // Helper interno para añadir líneas al historial de cambios sin borrar los anteriores
  registrarCambioLocal: (projectId, infoCambio) => {
    const logs = JSON.parse(localStorage.getItem('DYNAMIC_COMMITS_LOGS')) || {};
    if (!logs[projectId]) logs[projectId] = [];

    logs[projectId].unshift({
      id: 'c_' + Date.now(),
      autor: infoCambio.autor,
      mensaje: infoCambio.mensaje,
      fecha: new Date().toISOString().replace('T', ' ').substring(0, 16)
    });
    localStorage.setItem('DYNAMIC_COMMITS_LOGS', JSON.stringify(logs));
  }
};