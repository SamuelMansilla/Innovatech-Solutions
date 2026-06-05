package ms.example.ms_proyectos.service;

import ms.example.ms_proyectos.events.ProjectEvent;
import ms.example.ms_proyectos.kafka.KafkaProducerService;
import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.repository.ProyectoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProyectoServiceEventTest {

    @Mock
    ProyectoRepository proyectoRepository;

    @Mock
    KafkaProducerService kafkaProducerService;

    @InjectMocks
    ProyectoService proyectoService;

    @Test
    void crearPublishesEvent() {
        Proyecto p = new Proyecto();
        p.setId(1L);
        p.setNombre("Test");
        p.setDescripcion("Desc");
        p.setEstado(EstadoProyecto.ACTIVO);
        p.setFechaCreacion(LocalDate.now());
        p.setFechaActualizacion(LocalDate.now());

        when(proyectoRepository.existsByNombreIgnoreCase(any())).thenReturn(false);
        when(proyectoRepository.save(any())).thenReturn(p);

        Proyecto toCreate = new Proyecto();
        toCreate.setNombre("Test");
        toCreate.setDescripcion("Desc");
        toCreate.setEstado(EstadoProyecto.ACTIVO);
        proyectoService.crear(toCreate);

        verify(kafkaProducerService, times(1)).sendEvent(any(ProjectEvent.class));
    }

    @Test
    void actualizarPublishesEvent() {
        Proyecto existing = new Proyecto();
        existing.setId(1L);
        existing.setNombre("Old");
        existing.setDescripcion("Old");
        existing.setEstado(EstadoProyecto.ACTIVO);

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(proyectoRepository.save(any())).thenReturn(existing);

        proyectoService.actualizar(1L, existing);

        verify(kafkaProducerService, times(1)).sendEvent(any(ProjectEvent.class));
    }
}
