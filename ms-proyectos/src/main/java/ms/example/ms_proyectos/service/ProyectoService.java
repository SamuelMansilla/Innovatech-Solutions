package ms.example.ms_proyectos.service;

import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.repository.ProyectoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    // Inyección de dependencias por constructor (mejor práctica en Spring Boot)
    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    /**
     * Obtiene todos los proyectos
     * @return lista de todos los proyectos
     */
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    /**
     * Obtiene un proyecto por su ID
     * @param id el ID del proyecto
     * @return Optional con el proyecto si existe
     */
    public Optional<Proyecto> obtenerPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    /**
     * Busca proyectos por nombre
     * @param nombre el nombre a buscar
     * @return lista de proyectos que coinciden
     */
    public List<Proyecto> buscarPorNombre(String nombre) {
        return proyectoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene proyectos con filtros opcionales y paginación.
     */
    public Page<Proyecto> obtenerConFiltros(String nombre,
                                           EstadoProyecto estado,
                                           LocalDate fechaInicioDesde,
                                           LocalDate fechaFinHasta,
                                           Pageable pageable) {
        return proyectoRepository.search(nombre, estado, fechaInicioDesde, fechaFinHasta, pageable);
    }

    /**
     * Obtiene proyectos filtrados por estado
     * @param estado el estado a filtrar
     * @return lista de proyectos con ese estado
     */
    public List<Proyecto> obtenerPorEstado(EstadoProyecto estado) {
        return proyectoRepository.findByEstado(estado);
    }

    /**
     * Crea un nuevo proyecto
     * @param proyecto el proyecto a crear
     * @return el proyecto creado con ID asignado
     * @throws IllegalArgumentException si el nombre ya existe o hay validación fallida
     */
    public Proyecto crear(Proyecto proyecto) {
        validarProyecto(proyecto);
        
        if (proyectoRepository.existsByNombreIgnoreCase(proyecto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un proyecto con el nombre: " + proyecto.getNombre());
        }

        proyecto.setFechaCreacion(LocalDate.now());
        proyecto.setFechaActualizacion(LocalDate.now());
        return proyectoRepository.save(proyecto);
    }

    /**
     * Actualiza un proyecto existente
     * @param id el ID del proyecto a actualizar
     * @param proyectoActualizado los datos actualizados
     * @return el proyecto actualizado
     * @throws IllegalArgumentException si el proyecto no existe
     */
    public Proyecto actualizar(Long id, Proyecto proyectoActualizado) {
        validarProyecto(proyectoActualizado);

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + id));

        proyecto.setNombre(proyectoActualizado.getNombre());
        proyecto.setDescripcion(proyectoActualizado.getDescripcion());
        proyecto.setEstado(proyectoActualizado.getEstado());
        proyecto.setFechaInicio(proyectoActualizado.getFechaInicio());
        proyecto.setFechaFin(proyectoActualizado.getFechaFin());
        proyecto.setFechaActualizacion(LocalDate.now());

        return proyectoRepository.save(proyecto);
    }

    /**
     * Elimina un proyecto
     * @param id el ID del proyecto a eliminar
     * @throws IllegalArgumentException si el proyecto no existe
     */
    public void eliminar(Long id) {
        if (!proyectoRepository.existsById(id)) {
            throw new IllegalArgumentException("Proyecto no encontrado con ID: " + id);
        }
        proyectoRepository.deleteById(id);
    }

    /**
     * Valida los datos básicos del proyecto
     * @param proyecto el proyecto a validar
     * @throws IllegalArgumentException si la validación falla
     */
    private void validarProyecto(Proyecto proyecto) {
        if (proyecto.getNombre() == null || proyecto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
        }
        if (proyecto.getEstado() == null) {
            throw new IllegalArgumentException("El estado del proyecto es obligatorio");
        }
        if (proyecto.getNombre().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }
        validarFechas(proyecto);
    }

    private void validarFechas(Proyecto proyecto) {
        if (proyecto.getFechaInicio() != null && proyecto.getFechaFin() != null
                && proyecto.getFechaInicio().isAfter(proyecto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }
}
