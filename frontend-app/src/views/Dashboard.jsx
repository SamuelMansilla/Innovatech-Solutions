// src/views/Dashboard.jsx
import React, { useState } from 'react';
import { useTalentViewModel } from '../components/useTalentViewModel';
import { useProjectViewModel } from '../components/useProjectViewModel';
import { useAnalyticsViewModel } from '../components/useAnalyticsViewModel';
import { EmployeeTable } from '../components/EmployeeTable';
import { ProjectProgress } from '../components/ProjectProgress';
import { ProjectDetail } from '../components/ProjectDetail';
import { AnalyticsPanel } from '../components/AnalyticsPanel';
// 1. Importamos el nuevo formulario
import { ProyectosFrom } from './ProyectosFrom';

export function Dashboard() {
  // Agregamos 'nuevo-proyecto' como un estado posible
  const [activeTab, setActiveTab] = useState('proyectos'); 
  
  const talentVM = useTalentViewModel();
  const projectVM = useProjectViewModel();
  const analyticsVM = useAnalyticsViewModel(); 

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
          
          {/* 2. Nueva pestaña para crear proyectos */}
          <button 
            className={`tab-link ${activeTab === 'nuevo-proyecto' ? 'active' : ''}`}
            onClick={() => setActiveTab('nuevo-proyecto')}
          >
            🛠️ Nuevo Proyecto
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
        ) : activeTab === 'nuevo-proyecto' ? (
          /* 3. Renderizamos el componente ProyectosFrom */
          <div>
            <div className="section-title-container">
              <h2>Planificar Nuevo Microservicio / Proyecto</h2>
            </div>
            {/* Pasamos projectVM para que el formulario pueda usar agregarProyecto() */}
            <ProyectosFrom projectVM={projectVM} />
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