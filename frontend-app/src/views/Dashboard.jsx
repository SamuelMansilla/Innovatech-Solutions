// src/views/Dashboard.jsx
import React, { useState } from 'react';
import { useTalentViewModel } from '../components/useTalentViewModel';
import { useProjectViewModel } from '../components/useProjectViewModel';
import { useAnalyticsViewModel } from '../components/useAnalyticsViewModel'; // Nuevo Hook
import { EmployeeTable } from '../components/EmployeeTable';
import { ProjectProgress } from '../components/ProjectProgress';
import { ProjectDetail } from '../components/ProjectDetail';
import { AnalyticsPanel } from '../components/AnalyticsPanel'; // Nuevo Componente

export function Dashboard() {
  const [activeTab, setActiveTab] = useState('proyectos'); // 'proyectos', 'talento' o 'analitica'
  
  const talentVM = useTalentViewModel();
  const projectVM = useProjectViewModel();
  const analyticsVM = useAnalyticsViewModel(); // Instanciamos el ViewModel de analítica

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-main">
          <h1>Innovatech Solutions</h1>
          <p>Plataforma Integral de Gestión y Analítica Operativa (Estructura MVVM)</p>
        </div>
      </header>

      {!projectVM.selectedProject && (
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
          <button 
            className={`tab-link ${activeTab === 'analitica' ? 'active' : ''}`}
            onClick={() => setActiveTab('analitica')}
          >
            📈 Métricas e Informes
          </button>
        </nav>
      )}

      <main className="dashboard-main">
        {projectVM.selectedProject ? (
          <ProjectDetail 
            proyecto={projectVM.selectedProject} 
            commits={projectVM.commits} 
            onBack={projectVM.verDetalleProyecto} 
          />
        ) : activeTab === 'proyectos' ? (
          <div>
            <div className="section-title-container">
              <h2>Métricas de Desempeño y Cronogramas</h2>
              <span className="badge-count">{projectVM.proyectos.length} Activos</span>
            </div>
            <ProjectProgress 
              proyectos={projectVM.proyectos} 
              metrics={projectVM.metrics} 
              onSelectProject={projectVM.verDetalleProyecto} 
            />
          </div>
        ) : activeTab === 'talento' ? (
          <div>
            <div className="section-title-container">
              <h2>Disponibilidad y Asignación de Ingenieros</h2>
              <span className="badge-count">{talentVM.empleados.length} Registrados</span>
            </div>
            <EmployeeTable empleados={talentVM.empleados} loading={talentVM.loading} error={talentVM.error} />
          </div>
        ) : (
          /* NUEVA PESTAÑA CONECTADA AL INFORME */
          <div>
            <div className="section-title-container">
              <h2>Analítica Gerencial y Estado de Resiliencia</h2>
            </div>
            <AnalyticsPanel vm={analyticsVM} />
          </div>
        )}
      </main>
    </div>
  );
}