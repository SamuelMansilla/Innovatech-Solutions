// src/services/talentService.js

const API_BASE_URL = 'http://localhost:8082/api/v1/talento';

export const talentService = {
  /**
   * Obtiene la lista completa de profesionales desde ms-talento (Supabase)
   */
  getProfesionales: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/profesionales`);
      if (!response.ok) {
        throw new Error('Error al conectar con el microservicio de Talento');
      }
      return await response.json();
    } catch (error) {
      console.error('[Service Error] getProfesionales failed:', error);
      throw error;
    }
  }
};