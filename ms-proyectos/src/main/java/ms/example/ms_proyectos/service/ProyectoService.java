package ms.example.ms_proyectos.service;

import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.repository.ProyectoRepository;
import ms.example.common.events.ProjectEvent;
import ms.example.ms_proyectos.kafka.KafkaProducerService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final KafkaProducerService kafkaProducerService;

    // Inyección de dependencias por constructor (mejor práctica en Spring Boot)
    public ProyectoService(ProyectoRepository proyectoRepository, KafkaProducerService kafkaProducerService) {
        this.proyectoRepository = proyectoRepository;
        this.kafkaProducerService = kafkaProducerService;
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
        Proyecto saved = proyectoRepository.save(proyecto);

        // Publicar evento asincrónico
        ProjectEvent event = new ProjectEvent();
        event.setEventType("ProjectCreated");
        event.setId(saved.getId());
        event.setNombre(saved.getNombre());
        event.setDescripcion(saved.getDescripcion());
        event.setEstado(saved.getEstado().name());
        event.setFechaInicio(saved.getFechaInicio());
        event.setFechaFin(saved.getFechaFin());
        event.setFechaCreacion(saved.getFechaCreacion());
        event.setFechaActualizacion(saved.getFechaActualizacion());
        kafkaProducerService.sendEvent(event);

        return saved;
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

        Proyecto saved = proyectoRepository.save(proyecto);

        // Publicar evento de actualización
        ProjectEvent event = new ProjectEvent();
        event.setEventType("ProjectUpdated");
        event.setId(saved.getId());
        event.setNombre(saved.getNombre());
        event.setDescripcion(saved.getDescripcion());
        event.setEstado(saved.getEstado().name());
        event.setFechaInicio(saved.getFechaInicio());
        event.setFechaFin(saved.getFechaFin());
        event.setFechaCreacion(saved.getFechaCreacion());
        event.setFechaActualizacion(saved.getFechaActualizacion());
        kafkaProducerService.sendEvent(event);

        return saved;
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
    }
}
