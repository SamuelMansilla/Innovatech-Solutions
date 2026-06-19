// src/services/projectService.js

// Asumiendo que ms-proyectos correrá en el puerto 8083 (ya que talento tomó el 8082)
const API_BASE_URL = 'http://localhost:8083/api/v1/proyectos';

// Datos de prueba (Seed Data) alineados con los sectores críticos de Innovatech (Fintech, Retail, Gobierno)
const PROYECTOS_SEMILLA = [
  { id: 1, nombre: 'Reforma Sistema Fintech Core', descripcion: 'Migración de arquitectura monolítica a microservicios financieros.', estado: 'EN_PROGRESO', fechaInicio: '2026-01-10', fechaFin: '2026-12-20' },
  { id: 2, nombre: 'Portal de Cumplimiento Regulatorio', descripcion: 'Auditoría de fondos y cumplimiento para sector Gobierno.', estado: 'PENDIENTE', fechaInicio: '2026-07-01', fechaFin: '2026-11-15' },
  { id: 3, nombre: 'Plataforma Omnicanal Retail v2', descripcion: 'Optimización de pasarela de pagos y stock en tiempo real.', estado: 'COMPLETADO', fechaInicio: '2026-02-01', fechaFin: '2026-06-01' }
];

export const projectService = {
  getProyectos: async () => {
    try {
      const response = await fetch(API_BASE_URL);
      if (!response.ok) {
        throw new Error('Sin respuesta del servidor de proyectos. Usando datos locales de resiliencia.');
      }
      const data = await response.json();
      // Si el servidor responde pero la BD en memoria está vacía, usamos la semilla
      return data.length === 0 ? PROYECTOS_SEMILLA : data;
    } catch (error) {
      console.warn('[Service Warning] Usando datos semilla por caída del microservicio:', error.message);
      return PROYECTOS_SEMILLA; // Fallback elegante tipo Circuit Breaker en el Front
    }
  }
};