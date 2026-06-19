// src/components/ProjectProgress.jsx
import React from 'react';

export function ProjectProgress({ proyectos, metrics, onSelectProject }) {
  return (
    <div className="projects-section">
      {/* Grid de Métricas */}
      <div className="metrics-grid">
        <div className="metric-card shadow">
          <h3>Total Proyectos</h3>
          <p className="metric-value">{metrics.total}</p>
        </div>
        <div className="metric-card shadow progreso">
          <h3>En Progreso</h3>
          <p className="metric-value">{metrics.enProgreso}</p>
        </div>
        <div className="metric-card shadow completado">
          <h3>Completados</h3>
          <p className="metric-value">{metrics.completados}</p>
        </div>
        <div className="metric-card shadow alerta">
          <h3>Avance Global</h3>
          <p className="metric-value">{metrics.porcentajeAvance}%</p>
          <div className="progress-bar-container">
            <div className="progress-bar-fill" style={{ width: `${metrics.porcentajeAvance}%` }}></div>
          </div>
        </div>
      </div>

      {/* Grid de Tarjetas - Ahora actúan como Botones */}
      <div className="projects-grid">
        {proyectos.map((proy) => (
          <div 
            key={proy.id} 
            className="project-progress-card clickable shadow"
            onClick={() => onSelectProject(proy.id)} // Dispara la acción en el click
          >
            <div className="project-card-header">
              <h4>{proy.nombre}</h4>
              <span className={`status-badge ${proy.estado.toLowerCase()}`}>
                {proy.estado.replace('_', ' ')}
              </span>
            </div>
            <p className="project-description">{proy.descripcion}</p>
            <div className="project-timeline">
              <small>📅 {proy.fechaInicio}</small>
              <small>🏁 {proy.fechaFin}</small>
            </div>
            <div className="project-card-footer">
              <span className="action-hint">Ver historial de cambios →</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}