package ms.example.ms_proyectos.repository;

import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.model.EstadoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    /**
     * Busca proyectos por nombre (búsqueda insensible a mayúsculas/minúsculas)
     * @param nombre el nombre del proyecto
     * @return lista de proyectos que coinciden
     */
    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca proyectos por estado
     * @param estado el estado del proyecto (ej: ACTIVO, COMPLETADO, CANCELADO)
     * @return lista de proyectos con ese estado
     */
    List<Proyecto> findByEstado(EstadoProyecto estado);

        Page<Proyecto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

        Page<Proyecto> findByEstado(EstadoProyecto estado, Pageable pageable);

        Page<Proyecto> findByNombreContainingIgnoreCaseAndEstado(String nombre, EstadoProyecto estado, Pageable pageable);

        @Query("SELECT p FROM Proyecto p " +
            "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:estado IS NULL OR p.estado = :estado) " +
            "AND (:fechaInicioDesde IS NULL OR p.fechaInicio >= :fechaInicioDesde) " +
            "AND (:fechaFinHasta IS NULL OR p.fechaFin <= :fechaFinHasta)")
        Page<Proyecto> search(
            @Param("nombre") String nombre,
            @Param("estado") EstadoProyecto estado,
            @Param("fechaInicioDesde") LocalDate fechaInicioDesde,
            @Param("fechaFinHasta") LocalDate fechaFinHasta,
            Pageable pageable);

    /**
     * Verifica si existe un proyecto con el nombre especificado
     * @param nombre el nombre del proyecto
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombreIgnoreCase(String nombre);
}
