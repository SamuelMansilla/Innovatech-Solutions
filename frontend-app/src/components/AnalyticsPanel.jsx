// src/components/AnalyticsPanel.jsx
import React from 'react';

export function AnalyticsPanel({ vm }) {
  return (
    <div className="analytics-section animate-fade-in">
      {/* MONITOR DE SALUD DE LA ARQUITECTURA (CIRCUIT BREAKER LOGS) */}
      <div className="circuit-breaker-monitor shadow">
        <div className="monitor-header">
          <h3>⚡ Monitor de Resiliencia (Resilience4j - BFF)</h3>
          <span className={`circuit-pill ${vm.circuitStatus.toLowerCase()}`}>
            Circuito: {vm.circuitStatus}
          </span>
        </div>
        <p className="monitor-desc">
          {vm.circuitStatus === 'CLOSED' 
            ? '✅ El tráfico fluye normalmente hacia ms-analitica. Conexión síncrona estable.' 
            : '⚠️ CIRCUITO ABIERTO: ms-analitica no responde o lanzó una excepción (Factory Fallback activo).'}
        </p>
      </div>

      {/* FILTROS FACTORY METHOD */}
      <div className="analytics-controls shadow">
        <label>Seleccionar Sector de Negocio (Factory Method):</label>
        <div className="sector-selector-buttons">
          {['fintech', 'retail', 'gobierno'].map((s) => (
            <button
              key={s}
              className={`btn-sector ${vm.sector === s ? 'active' : ''}`}
              onClick={() => vm.setSector(s)}
              disabled={vm.loading}
            >
              💼 Reporte {s.toUpperCase()}
            </button>
          ))}
        </div>
      </div>

      {/* RENDERIZADO DEL REPORTE DINÁMICO */}
      <div className="report-display-container shadow">
        {vm.loading ? (
          <div className="loading-state">Procesando métricas en ms-analitica...</div>
        ) : vm.reporte ? (
          <div className={`report-card ${vm.circuitStatus === 'OPEN' ? 'degraded' : 'success'}`}>
            <div className="report-card-header">
              <h4>📋 Resultado del Reporte Operativo</h4>
              <span className="timestamp">Generación automática (Observer Active)</span>
            </div>
            
            {vm.circuitStatus === 'OPEN' ? (
              <div className="fallback-message-content">
                <p><strong>Respuesta de Respaldo Degradada:</strong></p>
                <p className="error-text">{vm.reporte.message}</p>
              </div>
            ) : (
              <div className="success-message-content">
                <p className="success-badge-text">✔ Conexión Exitosa</p>
                <pre className="json-code">
                  {JSON.stringify(vm.reporte, null, 2) || `Reporte crudo para sector ${vm.sector} procesado exitosamente por el Factory Method.`}
                </pre>
              </div>
            )}
          </div>
        ) : null}
      </div>
    </div>
  );
}