// src/services/analyticsService.js

const BFF_BASE_URL = 'http://localhost:8080/api/v1/bff/analytics';

export const analyticsService = {
  /**
   * Solicita un reporte al BFF usando el Factory Method del backend
   * @param {string} sector - 'fintech', 'retail', 'gobierno'
   */
  getReporteSector: async (sector) => {
    try {
      const response = await fetch(`${BFF_BASE_URL}/report?type=${sector}`);
      const data = await response.json();
      return data;
    } catch (error) {
      console.error('[Analytics Service Error]:', error);
      // Fallback local simulando la respuesta del Circuit Breaker degradado
      return {
        status: "degraded",
        message: "El servicio de analítica está temporalmente fuera de línea debido a una alta latencia. Activando respuesta de respaldo (Fallback)."
      };
    }
  }
};