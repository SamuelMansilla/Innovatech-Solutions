// src/services/projectService.js

const API_BASE_URL = 'http://localhost:8083/api/v1/proyectos';

const PROYECTOS_SEMILLA = [
  { id: 1, nombre: 'Reforma Sistema Fintech Core', descripcion: 'Migración de arquitectura monolítica a microservicios financieros.', estado: 'EN_PROGRESO', fechaInicio: '2026-01-10', fechaFin: '2026-12-20' },
  { id: 2, nombre: 'Portal de Cumplimiento Regulatorio', descripcion: 'Auditoría de fondos y cumplimiento para sector Gobierno.', estado: 'PENDIENTE', fechaInicio: '2026-07-01', fechaFin: '2026-11-15' },
  { id: 3, nombre: 'Plataforma Omnicanal Retail v2', descripcion: 'Optimización de pasarela de pagos y stock en tiempo real.', estado: 'COMPLETADO', fechaInicio: '2026-02-01', fechaFin: '2026-06-01' }
];

// Historial de cambios simulando la integración del Patrón Observer y eventos del Backend
const COMMITS_LOGS = {
  1: [
    { id: 'c1', autor: 'Samuel Mansilla', mensaje: 'feat: estructurar el patrón Repository para la persistencia core', fecha: '2026-06-15 14:30' },
    { id: 'c2', autor: 'Hans Gómez', mensaje: 'fix: corregir asignación de hilos en el Circuit Breaker de Analítica', fecha: '2026-06-16 09:15' },
    { id: 'c3', autor: 'Francisco Mardones', mensaje: 'docs: actualizar diagramas de secuencia del API Gateway', fecha: '2026-06-18 11:00' }
  ],
  2: [
    { id: 'c4', autor: 'Francisco Mardones', mensaje: 'feat: inicializar modelos de auditoría reguladora gubernamental', fecha: '2026-06-10 16:45' }
  ],
  3: [
    { id: 'c5', autor: 'Hans Gómez', mensaje: 'feat: implementar Factory Method para reportes dinámicos de Retail', fecha: '2026-06-05 18:22' },
    { id: 'c6', autor: 'Samuel Mansilla', mensaje: 'style: aplicar paleta de diseño sostenible #CBDF90 en el dashboard', fecha: '2026-06-09 10:45' }
  ]
};

export const projectService = {
  getProyectos: async () => {
    try {
      const response = await fetch(API_BASE_URL);
      if (!response.ok) throw new Error('Sin respuesta del servidor de proyectos.');
      const data = await response.json();
      return data.length === 0 ? PROYECTOS_SEMILLA : data;
    } catch (error) {
      return PROYECTOS_SEMILLA;
    }
  },

  // Nueva función del modelo para traer el historial
  getCommitsByProyecto: async (projectId) => {
    // Simula una llamada HTTP diferida a ms-analitica/ms-proyectos
    return COMMITS_LOGS[projectId] || [];
  }
};