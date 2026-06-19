// src/components/ProjectProgress.jsx
import React from 'react';

export function ProjectProgress({ proyectos, metrics }) {
  return (
    <div className="projects-section">
      {/* Tarjetas de Métricas de Control */}
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

      {/* Lista / Grid de estado individual de Proyectos */}
      <div className="projects-grid">
        {proyectos.map((proy) => (
          <div key={proy.id} className="project-progress-card shadow">
            <div className="project-card-header">
              <h4>{proy.nombre}</h4>
              <span className={`status-badge ${proy.estado.toLowerCase()}`}>
                {proy.estado.replace('_', ' ')}
              </span>
            </div>
            <p className="project-description">{proy.descripcion}</p>
            <div className="project-timeline">
              <small>📅 <strong>Inicio:</strong> {proy.fechaInicio}</small>
              <small>🏁 <strong>Entrega:</strong> {proy.fechaFin}</small>
            </div>
            {/* Barra de progreso visual por estado */}
            <div className="project-card-footer">
              <label>Progreso estimado:</label>
              <div className="progress-bar-container mini">
                <div 
                  className={`progress-bar-fill ${proy.estado.toLowerCase()}`} 
                  style={{ width: proy.estado === 'COMPLETADO' ? '100%' : proy.estado === 'EN_PROGRESO' ? '60%' : '5%' }}
                ></div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}