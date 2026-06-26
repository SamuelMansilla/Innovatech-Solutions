package ms.example.ms_proyectos.service;

import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.repository.ProyectoRepository;
import ms.example.ms_proyectos.exception.ResourceNotFoundException; 
import ms.example.common.events.ProjectEvent;
import ms.example.ms_proyectos.kafka.KafkaProducerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final KafkaProducerService kafkaProducerService;

    public ProyectoService(ProyectoRepository proyectoRepository, KafkaProducerService kafkaProducerService) {
        this.proyectoRepository = proyectoRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional(readOnly = true)
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Proyecto> obtenerPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Proyecto> buscarPorNombre(String nombre) {
        return proyectoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Page<Proyecto> obtenerConFiltros(String nombre,
                                           EstadoProyecto estado,
                                           LocalDate fechaInicioDesde,
                                           LocalDate fechaFinHasta,
                                           Pageable pageable) {
        return proyectoRepository.search(nombre, estado, fechaInicioDesde, fechaFinHasta, pageable);
    }

    @Transactional(readOnly = true)
    public List<Proyecto> obtenerPorEstado(EstadoProyecto estado) {
        return proyectoRepository.findByEstado(estado);
    }

    @Transactional
    public Proyecto crear(Proyecto proyecto) {
        validarProyecto(proyecto);
        
        if (proyectoRepository.existsByNombreIgnoreCase(proyecto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un proyecto con el nombre: " + proyecto.getNombre());
        }

        proyecto.setFechaCreacion(LocalDate.now());
        proyecto.setFechaActualizacion(LocalDate.now());
        
        Proyecto saved = proyectoRepository.save(proyecto);

        // COMENTADO TEMPORALMENTE PARA EVITAR ERROR 500 SI KAFKA NO ESTÁ ENCENDIDO
        // publicarEvento(saved, "ProjectCreated");

        return saved;
    }

    @Transactional
    public Proyecto actualizar(Long id, Proyecto proyectoActualizado) {
        validarProyecto(proyectoActualizado);

        Proyecto proyectoExistente = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + id));

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

        // COMENTADO TEMPORALMENTE PARA EVITAR ERROR 500 SI KAFKA NO ESTÁ ENCENDIDO
        // publicarEvento(saved, "ProjectUpdated");

        return saved;
    }

    @Transactional
    public void eliminar(Long id) {
        if (!proyectoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Proyecto no encontrado con ID: " + id);
        }
        proyectoRepository.deleteById(id);
    }

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
        
        if (proyecto.getFechaInicio() != null && proyecto.getFechaFin() != null) {
            if (proyecto.getFechaInicio().isAfter(proyecto.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de finalización.");
            }
        }
        validarFechas(proyecto);
    }

    private void validarFechas(Proyecto proyecto) {
        if (proyecto.getFechaInicio() != null && proyecto.getFechaFin() != null
                && proyecto.getFechaInicio().isAfter(proyecto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }

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