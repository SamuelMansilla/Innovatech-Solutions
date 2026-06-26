// src/components/ProjectDetail.jsx
import React from 'react';

export function ProjectDetail({ proyecto, commits, onBack }) {
  if (!proyecto) return null;

  const estadoClass = proyecto.estado ? proyecto.estado.toLowerCase() : 'pendiente';
  const estadoTexto = proyecto.estado ? proyecto.estado.replace('_', ' ') : 'Desconocido';

  return (
    <div className="project-summary-card shadow animate-fade-in" style={{ width: '100%', boxSizing: 'border-box' }}>
      
      <button className="btn-back" onClick={() => onBack(null)}>
        ← Volver a Avance de Proyectos
      </button>

      <div style={{ marginTop: '1.5rem', paddingBottom: '1.5rem', borderBottom: '1px solid var(--border-color)' }}>
        <h2 style={{ margin: '0 0 1rem 0', color: 'var(--text-main)', fontSize: '1.8rem' }}>
          {proyecto.nombre}
        </h2>
        
        <div className="meta-info">
          <span className={`status-badge ${estadoClass}`}>
            {estadoTexto}
          </span>
          <span>📅 Inicio: {proyecto.fechaInicio}</span>
          <span>🏁 Fin: {proyecto.fechaFin}</span>
        </div>

        <p className="desc" style={{ marginTop: '1rem', lineHeight: '1.6', color: 'var(--text-muted)' }}>
          {proyecto.descripcion}
        </p>
      </div>

      <div className="timeline-container">
        <h3 style={{ color: 'var(--text-main)', marginBottom: '1.5rem' }}>
          Bitácora de Tareas y Cambios
        </h3>

        {commits && commits.length > 0 ? (
          <div className="timeline">
            {commits.map((commit, index) => (
              <div key={commit.id || index} className="timeline-item">
                <div className="timeline-badge"></div>
                
                {/* Reemplazamos el fondo fijo por transparente para que tome el color de tu Dashboard (--bg-surface) */}
                <div className="timeline-card shadow" style={{ backgroundColor: 'transparent', border: '1px solid var(--border-color)' }}>
                  <div className="timeline-header">
                    <strong className="commit-author">
                      👤 {commit.autor || 'Usuario'}
                    </strong>
                    <span className="commit-date">🕒 {commit.fecha}</span>
                  </div>
                  
                  {/* Caja del mensaje: Usamos tu variable de fondo general (--bg-primary) para que no sea negra */}
                  <div className="commit-message-box" style={{ 
                    backgroundColor: 'var(--bg-primary)', 
                    border: '1px solid var(--border-color)',
                    marginTop: '0.8rem',
                    padding: '14px',
                    borderRadius: '8px'
                  }}>
                    <code style={{ 
                      fontFamily: 'inherit', 
                      fontSize: '0.95rem', 
                      whiteSpace: 'pre-wrap',
                      display: 'block'
                    }}>
                      {commit.mensaje}
                    </code>
                  </div>
                </div>
                
              </div>
            ))}
          </div>
        ) : (
          <div className="no-data-row" style={{ backgroundColor: 'var(--bg-primary)', border: '1px dashed var(--border-color)', borderRadius: '8px' }}>
            Aún no hay registros de tareas o cambios para este proyecto.
          </div>
        )}
      </div>
    </div>
  );
}