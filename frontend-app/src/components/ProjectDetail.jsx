// src/components/ProjectDetail.jsx
import React from 'react';

export function ProjectDetail({ proyecto, commits, onBack }) {
  return (
    <div className="project-detail-view animate-fade-in">
      <button className="btn-back" onClick={() => onBack(null)}>
        ⬅ Volver al Dashboard
      </button>

      <div className="project-summary-card shadow">
        <h2>{proyecto.nombre}</h2>
        <p className="desc">{proyecto.descripcion}</p>
        <div className="meta-info">
          <span>Estado: <strong className={`status-badge ${proyecto.estado.toLowerCase()}`}>{proyecto.estado}</strong></span>
          <span>Plazo: {proyecto.fechaInicio} al {proyecto.fechaFin}</span>
        </div>
      </div>

      <div className="timeline-container">
        <h3>🛠 Registro de Cambios e Integración Continua (Auditoría)</h3>
        {commits.length === 0 ? (
          <p className="no-data-row">No se registran commits recientes en este proyecto.</p>
        ) : (
          <div className="timeline">
            {commits.map((commit) => (
              <div key={commit.id} className="timeline-item">
                <div className="timeline-badge"></div>
                <div className="timeline-card shadow">
                  <div className="timeline-header">
                    <strong className="commit-author">👤 {commit.autor}</strong>
                    <span className="commit-date">🕒 {commit.fecha}</span>
                  </div>
                  <div className="commit-message-box">
                    <code>{commit.mensaje}</code>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}