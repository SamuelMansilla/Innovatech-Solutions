// src/views/Dashboard.jsx
import React, { useState } from 'react';
import { useTalentViewModel } from '../components/useTalentViewModel';
import { useProjectViewModel } from '../components/useProjectViewModel';
import { EmployeeTable } from '../components/EmployeeTable';
import { ProjectProgress } from '../components/ProjectProgress';

export function Dashboard() {
  const [activeTab, setActiveTab] = useState('proyectos'); // 'proyectos' o 'talento'
  
  // Consumo de ViewModels (Desacoplamiento MVVM perfecto)
  const talentVM = useTalentViewModel();
  const projectVM = useProjectViewModel();

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-main">
          <h1>Innovatech Solutions</h1>
          <p>Plataforma Integral de Gestión y Analítica Operativa (Estructura MVVM)</p>
        </div>
        <div className="header-actions">
          <button 
            className="btn-refresh" 
            onClick={() => activeTab === 'proyectos' ? projectVM.refresh() : talentVM.refresh()}
            disabled={projectVM.loading || talentVM.loading}
          >
            🔄 Sincronizar Datos
          </button>
        </div>
      </header>

      {/* Navegación por pestañas del Dashboard */}
      <nav className="dashboard-tabs">
        <button 
          className={`tab-link ${activeTab === 'proyectos' ? 'active' : ''}`}
          onClick={() => setActiveTab('proyectos')}
        >
          📊 Avance de Proyectos
        </button>
        <button 
          className={`tab-link ${activeTab === 'talento' ? 'active' : ''}`}
          onClick={() => setActiveTab('talento')}
        >
          👥 Células de Talento
        </button>
      </nav>

      <main className="dashboard-main">
        {activeTab === 'proyectos' ? (
          <div>
            <div className="section-title-container">
              <h2>Métricas de Desempeño y Cronogramas</h2>
              <span className="badge-count">{projectVM.proyectos.length} Activos</span>
            </div>
            <ProjectProgress proyectos={projectVM.proyectos} metrics={projectVM.metrics} />
          </div>
        ) : (
          <div>
            <div className="section-title-container">
              <h2>Disponibilidad y Asignación de Ingenieros</h2>
              <span className="badge-count">{talentVM.empleados.length} Registrados</span>
            </div>
            <EmployeeTable empleados={talentVM.empleados} loading={talentVM.loading} error={talentVM.error} />
          </div>
        )}
      </main>
    </div>
  );
}