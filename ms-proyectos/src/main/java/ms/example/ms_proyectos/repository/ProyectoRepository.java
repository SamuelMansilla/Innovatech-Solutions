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

    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);

    List<Proyecto> findByEstado(EstadoProyecto estado);

    Page<Proyecto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Proyecto> findByEstado(EstadoProyecto estado, Pageable pageable);

    Page<Proyecto> findByNombreContainingIgnoreCaseAndEstado(String nombre, EstadoProyecto estado, Pageable pageable);

    // SOLUCIÓN: Añadimos CAST explícitos para que PostgreSQL (Supabase) no confunda los null con "bytea" (binarios)
    @Query("SELECT p FROM Proyecto p " +
        "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%'))) " +
        "AND (:estado IS NULL OR p.estado = :estado) " +
        "AND (CAST(:fechaInicioDesde AS date) IS NULL OR p.fechaInicio >= :fechaInicioDesde) " +
        "AND (CAST(:fechaFinHasta AS date) IS NULL OR p.fechaFin <= :fechaFinHasta)")
    Page<Proyecto> search(
        @Param("nombre") String nombre,
        @Param("estado") EstadoProyecto estado,
        @Param("fechaInicioDesde") LocalDate fechaInicioDesde,
        @Param("fechaFinHasta") LocalDate fechaFinHasta,
        Pageable pageable);

    boolean existsByNombreIgnoreCase(String nombre);
}