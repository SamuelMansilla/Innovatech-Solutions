// src/services/analyticsService.js

// El Front se conecta directamente al puerto 8080 del orquestador BFF
const BFF_BASE_URL = 'http://localhost:8080/api/v1/bff/analytics';

export const analyticsService = {
  /**
   * Solicita un reporte al BFF usando el Factory Method del backend.
   * Cuenta con manejo robusto para capturar las respuestas del Circuit Breaker.
   * @param {string} sector - 'fintech', 'retail', 'gobierno'
   */
  getReporteSector: async (sector) => {
    try {
      const response = await fetch(`${BFF_BASE_URL}/report?type=${sector}`);
      
      // CONTROL DE SEGURIDAD CRÍTICO:
      // Si el BFF responde con un error HTTP (como el 503 del fallback de Resilience4j),
      // response.ok será false. Interceptamos el JSON de error para el ViewModel.
      if (!response.ok) {
        const errorData = await response.json();
        return errorData;
      }
      
      // Camino feliz: El backend respondió exitosamente (HTTP 200)
      const data = await response.json();
      return data;
    } catch (error) {
      console.error('[Analytics Service Error]:', error);
      
      // Fallback de contingencia en el cliente:
      // Se activa únicamente si el microservicio BFF completo está apagado o incomunicado.
      return {
        status: "degraded",
        message: "El orquestador BFF no responde. Activando respuesta de resiliencia en el cliente de forma automática."
      };
    }
  }
};