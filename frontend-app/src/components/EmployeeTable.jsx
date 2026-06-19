// src/components/EmployeeTable.jsx
import React from 'react';

export function EmployeeTable({ empleados, loading, error }) {
  if (loading && empleados.length === 0) {
    return <div className="loading-state">Cargando talento desde Supabase...</div>;
  }

  if (error) {
    return (
      <div className="error-card">
        <p>⚠️ <strong>Error en el módulo de Talento:</strong> {error}</p>
      </div>
    );
  }

  return (
    <div className="table-responsive">
      <table className="employee-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre Completo</th>
            <th>Rol / Especialidad</th>
            <th>Horas Disponibles</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          {empleados.length === 0 ? (
            <tr>
              <td colSpan="5" className="no-data-row">No hay profesionales registrados en el microservicio.</td>
            </tr>
          ) : (
            empleados.map((emp) => (
              <tr key={emp.id || emp.nombre}>
                <td>#{emp.id || 'N/A'}</td>
                <td><div className="employee-name">{emp.nombre}</div></td>
                <td><span className="badge-role">{emp.rol}</span></td>
                <td className="hours-cell"><strong>{emp.horasDisponibles} hrs</strong> / sem</td>
                <td>
                  <span className={`status-pill ${emp.horasDisponibles > 0 ? 'available' : 'allocated'}`}>
                    {emp.horasDisponibles > 0 ? 'Disponible' : 'Asignado'}
                  </span>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}