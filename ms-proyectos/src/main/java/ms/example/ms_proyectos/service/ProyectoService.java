package ms.example.ms_proyectos.service;

import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.repository.ProyectoRepository;
import ms.example.ms_proyectos.exception.ResourceNotFoundException; // Asegura importar tu excepción personalizada
import ms.example.common.events.ProjectEvent;
import ms.example.ms_proyectos.kafka.KafkaProducerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final KafkaProducerService kafkaProducerService;

    // Inyección de dependencias por constructor
    public ProyectoService(ProyectoRepository proyectoRepository, KafkaProducerService kafkaProducerService) {
        this.proyectoRepository = proyectoRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Obtiene todos los proyectos
     */
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    /**
     * Obtiene un proyecto por su ID
     */
    @Transactional(readOnly = true)
    public Optional<Proyecto> obtenerPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    /**
     * Busca proyectos por coincidencia parcial de nombre
     */
    @Transactional(readOnly = true)
    public List<Proyecto> buscarPorNombre(String nombre) {
        return proyectoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene proyectos filtrados por estado
     */
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerPorEstado(EstadoProyecto estado) {
        return proyectoRepository.findByEstado(estado);
    }

    /**
     * Crea un nuevo proyecto, valida reglas de negocio y publica evento en Kafka
     */
    @Transactional
    public Proyecto crear(Proyecto proyecto) {
        validarProyecto(proyecto);
        
        if (proyectoRepository.existsByNombreIgnoreCase(proyecto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un proyecto con el nombre: " + proyecto.getNombre());
        }

        proyecto.setFechaCreacion(LocalDate.now());
        proyecto.setFechaActualizacion(LocalDate.now());
        
        Proyecto saved = proyectoRepository.save(proyecto);

        // Notificación Event-Driven
        publicarEvento(saved, "ProjectCreated");

        return saved;
    }

    /**
     * Actualiza un proyecto existente, verifica coherencia y notifica la actualización
     */
    @Transactional
    public Proyecto actualizar(Long id, Proyecto proyectoActualizado) {
        validarProyecto(proyectoActualizado);

        Proyecto proyectoExistente = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + id));

        // Validación extra: Si el nombre cambia, verificar que no esté duplicado con otro proyecto
        if (!proyectoExistente.getNombre().equalsIgnoreCase(proyectoActualizado.getNombre()) &&
            proyectoRepository.existsByNombreIgnoreCase(proyectoActualizado.getNombre())) {
            throw new IllegalArgumentException("El nuevo nombre ya está siendo usado por otro proyecto.");
        }

        proyectoExistente.setNombre(proyectoActualizado.getNombre());
        proyectoExistente.setDescripcion(proyectoActualizado.getDescripcion());
        proyectoExistente.setEstado(proyectoActualizado.getEstado());
        proyectoExistente.setFechaInicio(proyectoActualizado.getFechaInicio());
        proyectoExistente.setFechaFin(proyectoActualizado.getFechaFin());
        proyectoExistente.setFechaActualizacion(LocalDate.now());

        Proyecto saved = proyectoRepository.save(proyectoExistente);

        // Notificación Event-Driven
        publicarEvento(saved, "ProjectUpdated");

        return saved;
    }

    /**
     * Elimina un proyecto por ID
     */
    @Transactional
    public void eliminar(Long id) {
        if (!proyectoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Proyecto no encontrado con ID: " + id);
        }
        proyectoRepository.deleteById(id);
    }

    /**
     * Centraliza las validaciones de negocio obligatorias y límites de auditoría (Sprint 1)
     */
    private void validarProyecto(Proyecto proyecto) {
        if (proyecto.getNombre() == null || proyecto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
        }
        if (proyecto.getNombre().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder los 100 caracteres");
        }
        if (proyecto.getEstado() == null) {
            throw new IllegalArgumentException("El estado del proyecto es obligatorio");
        }
        
        // Nueva Validación de Negocio Cruzada: Coherencia de Fechas
        if (proyecto.getFechaInicio() != null && proyecto.getFechaFin() != null) {
            if (proyecto.getFechaInicio().isAfter(proyecto.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de finalización.");
            }
        }
    }

    /**
     * Helper privado para encapsular la construcción y mapeo del evento de Kafka (Principio DRY)
     */
    private void publicarEvento(Proyecto proyecto, String eventType) {
        ProjectEvent event = new ProjectEvent();
        event.setEventType(eventType);
        event.setId(proyecto.getId());
        event.setNombre(proyecto.getNombre());
        event.setDescripcion(proyecto.getDescripcion());
        event.setEstado(proyecto.getEstado().name());
        event.setFechaInicio(proyecto.getFechaInicio());
        event.setFechaFin(proyecto.getFechaFin());
        event.setFechaCreacion(proyecto.getFechaCreacion());
        event.setFechaActualizacion(proyecto.getFechaActualizacion());
        
        kafkaProducerService.sendEvent(event);
    }
}