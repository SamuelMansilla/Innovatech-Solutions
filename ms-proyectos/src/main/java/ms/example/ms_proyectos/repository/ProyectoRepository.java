package ms.example.ms_proyectos.repository;

import ms.example.ms_proyectos.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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
    List<Proyecto> findByEstado(String estado);

    /**
     * Verifica si existe un proyecto con el nombre especificado
     * @param nombre el nombre del proyecto
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombreIgnoreCase(String nombre);
}
