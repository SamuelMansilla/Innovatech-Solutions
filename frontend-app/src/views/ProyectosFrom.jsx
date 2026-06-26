// src/views/ProyectosFrom.jsx
import React, { useState } from 'react';

export function ProyectosFrom({ projectVM }) {
  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    estado: 'PLANIFICADO',
    tareas: '' // Campo para el registro manual
  });

  const [errorLocal, setErrorLocal] = useState(null);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorLocal(null);

    if (new Date(formData.fechaInicio) > new Date(formData.fechaFin)) {
      setErrorLocal('La fecha de inicio no puede ser posterior a la fecha de fin.');
      return;
    }

    // Guardamos el proyecto enviando también las tareas
    const resultado = await projectVM.agregarProyecto(formData);
    if (resultado && resultado.success) {
      alert('¡Proyecto y tareas guardados con éxito!');
      setFormData({ nombre: '', descripcion: '', fechaInicio: '', fechaFin: '', estado: 'PLANIFICADO', tareas: '' });
    }
  };

  return (
    <div style={{ 
      width: '100%', 
      boxSizing: 'border-box', 
      textAlign: 'left',
      backgroundColor: '#ffffff', // Fondo blanco limpio
      padding: '24px',
      borderRadius: '8px',
      border: '1px solid #e1e1e1'
    }}>
      
      {(errorLocal || projectVM.error) && (
        <div style={{ 
          color: '#c53030', 
          backgroundColor: '#fff5f5', 
          padding: '12px 16px', 
          borderRadius: '6px', 
          borderLeft: '4px solid #f56565',
          fontSize: '0.9em',
          marginBottom: '20px',
          fontWeight: '500'
        }}>
          ⚠️ {errorLocal || projectVM.error}
        </div>
      )}

      <form onSubmit={handleSubmit} style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '24px',
        width: '100%'
      }}>
        
        <div style={groupStyle}>
          <label style={labelStyle}>Nombre del Proyecto</label>
          <input 
            type="text" 
            name="nombre" 
            placeholder="Ej. Reforma Sistema Fintech Core"
            value={formData.nombre} 
            onChange={handleChange} 
            style={inputStyle} 
            required 
          />
        </div>

        <div style={groupStyle}>
          <label style={labelStyle}>Descripción del Alcance</label>
          <textarea 
            name="descripcion" 
            placeholder="Detalla los objetivos estratégicos y el alcance técnico del microservicio..."
            value={formData.descripcion} 
            onChange={handleChange} 
            style={{ ...inputStyle, minHeight: '60px', resize: 'vertical' }} 
            required 
          />
        </div>

        {/* SECCIÓN DE REGISTRO MANUAL */}
        <div style={{ 
          marginTop: '8px',
          padding: '16px', 
          backgroundColor: '#f9f9f9',
          borderRadius: '8px',
          border: '1px solid #e1e1e1'
        }}>
          <div style={groupStyle}>
            <label style={{ ...labelStyle, color: '#444' }}>Tareas o Acciones Realizadas (Registro Manual)</label>
            <textarea 
              name="tareas" 
              placeholder="Enumere las tareas clave o acciones realizadas para este proyecto o actualización..."
              value={formData.tareas} 
              onChange={handleChange} 
              style={{ ...inputStyle, minHeight: '90px', resize: 'vertical' }} 
              required
            />
          </div>
        </div>

        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: '1fr 1fr 1fr', 
          gap: '20px',
          alignItems: 'end'
        }}>
          {/* VUELVE EL TRUCO DEL CALENDARIO */}
          <div style={groupStyle}>
            <label style={labelStyle}>Fecha de Inicio</label>
            <input 
              type="date" 
              name="fechaInicio" 
              value={formData.fechaInicio} 
              onChange={handleChange} 
              onClick={(e) => e.target.showPicker && e.target.showPicker()} 
              style={{ ...inputStyle, cursor: 'pointer' }} 
              required 
            />
          </div>

          <div style={groupStyle}>
            <label style={labelStyle}>Fecha de Fin</label>
            <input 
              type="date" 
              name="fechaFin" 
              value={formData.fechaFin} 
              onChange={handleChange} 
              onClick={(e) => e.target.showPicker && e.target.showPicker()} 
              style={{ ...inputStyle, cursor: 'pointer' }} 
              required 
            />
          </div>

          <div style={groupStyle}>
            <label style={labelStyle}>Estado Operativo Inicial</label>
            <select 
              name="estado" 
              value={formData.estado} 
              onChange={handleChange} 
              style={{ ...inputStyle, cursor: 'pointer' }}
            >
              {/* VUELVEN LOS ESTADOS CORRECTOS DE JAVA */}
              <option value="PLANIFICADO">Planificado</option>
              <option value="ACTIVO">Activo (En Progreso)</option>
              <option value="PAUSADO">Pausado</option>
              <option value="COMPLETADO">Completado</option>
              <option value="CANCELADO">Cancelado</option>
            </select>
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '10px' }}>
          <button 
            type="submit" 
            disabled={projectVM.loading}
            style={{ 
              backgroundColor: '#417b3c', // Volví a poner el Verde Corporativo por si acaso, puedes cambiarlo si prefieres azul
              color: 'white', 
              padding: '12px 32px', 
              border: 'none', 
              borderRadius: '6px', 
              cursor: projectVM.loading ? 'not-allowed' : 'pointer', 
              fontWeight: '600',
              fontSize: '0.95em',
              transition: 'background-color 0.2s ease, transform 0.1s ease',
              opacity: projectVM.loading ? 0.6 : 1
            }}
            onMouseOver={(e) => { if (!projectVM.loading) e.target.style.backgroundColor = '#2d5a28'; }}
            onMouseOut={(e) => { if (!projectVM.loading) e.target.style.backgroundColor = '#417b3c'; }}
          >
            {projectVM.loading ? 'Sincronizando...' : 'Guardar Proyecto'}
          </button>
        </div>

      </form>
    </div>
  );
}

// Estilos
const groupStyle = { display: 'flex', flexDirection: 'column', gap: '6px' };
const labelStyle = { fontSize: '0.9em', color: '#555', fontWeight: '500' };
const inputStyle = {
  width: '100%', padding: '12px 14px', backgroundColor: 'white',
  color: '#333', border: '1px solid #ccc', borderRadius: '6px', 
  boxSizing: 'border-box', outline: 'none', fontSize: '0.95em',
  transition: 'border-color 0.2s ease'
};