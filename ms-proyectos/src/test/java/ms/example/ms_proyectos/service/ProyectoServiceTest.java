package ms.example.ms_proyectos.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.repository.ProyectoRepository;
import ms.example.ms_proyectos.exception.ResourceNotFoundException;
import ms.example.ms_proyectos.kafka.KafkaProducerService;
import ms.example.common.events.ProjectEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProyectoServiceTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ProyectoService proyectoService;

    private Proyecto proyectoBase;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de cada @Test para tener un objeto fresco y evitar código duplicado
        proyectoBase = new Proyecto(
                "Proyecto Alpha", 
                "Descripción", 
                EstadoProyecto.ACTIVO, 
                LocalDate.now(), 
                LocalDate.now().plusMonths(1)
        );
        proyectoBase.setId(1L);
    }

    // 1. PRUEBA: Obtener todos los proyectos
    @Test
    public void obtenerTodos_DebeRetornarListaDeProyectos() {
        when(proyectoRepository.findAll()).thenReturn(List.of(proyectoBase));

        List<Proyecto> result = proyectoService.obtenerTodos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Proyecto Alpha", result.get(0).getNombre());
        verify(proyectoRepository, times(1)).findAll();
    }

    // 2. PRUEBA: Obtener proyecto por ID existente
    @Test
    public void obtenerPorId_CuandoExiste_RetornaProyecto() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoBase));

        Optional<Proyecto> result = proyectoService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("Proyecto Alpha", result.get().getNombre());
        verify(proyectoRepository, times(1)).findById(1L);
    }

    // 3. PRUEBA: Obtener proyecto por ID inexistente
    @Test
    public void obtenerPorId_CuandoNoExiste_RetornaVacio() {
        when(proyectoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Proyecto> result = proyectoService.obtenerPorId(999L);

        assertFalse(result.isPresent());
        verify(proyectoRepository, times(1)).findById(999L);
    }

    // 4. PRUEBA: Buscar proyecto por nombre
    @Test
    public void buscarPorNombre_RetornaProyectosCoincidentes() {
        when(proyectoRepository.findByNombreContainingIgnoreCase("Alpha")).thenReturn(List.of(proyectoBase));

        List<Proyecto> result = proyectoService.buscarPorNombre("Alpha");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Proyecto Alpha", result.get(0).getNombre());
        verify(proyectoRepository, times(1)).findByNombreContainingIgnoreCase("Alpha");
    }

    // 5. PRUEBA: Obtener proyectos por estado
    @Test
    public void obtenerPorEstado_RetornaProyectosDelEstado() {
        when(proyectoRepository.findByEstado(EstadoProyecto.ACTIVO)).thenReturn(List.of(proyectoBase));

        List<Proyecto> result = proyectoService.obtenerPorEstado(EstadoProyecto.ACTIVO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(EstadoProyecto.ACTIVO, result.get(0).getEstado());
        verify(proyectoRepository, times(1)).findByEstado(EstadoProyecto.ACTIVO);
    }

    // 6. PRUEBA: Crear proyecto con datos válidos
    @Test
    public void crear_ConDatosValidos_GuardaProyectoYEnviaEventoKafka() {
        when(proyectoRepository.existsByNombreIgnoreCase(proyectoBase.getNombre())).thenReturn(false);
        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyectoBase);

        Proyecto creado = proyectoService.crear(proyectoBase);

        assertNotNull(creado);
        assertEquals("Proyecto Alpha", creado.getNombre());
        verify(proyectoRepository, times(1)).save(any(Proyecto.class));

        // Verificamos que se haya enviado el evento a Kafka
        ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
        verify(kafkaProducerService, times(1)).sendEvent(captor.capture());
        assertEquals("ProjectCreated", captor.getValue().getEventType());
    }

    // 7. PRUEBA: Crear proyecto con nombre vacío (validación de negocio)
    @Test
    public void crear_ConNombreVacio_LanzaIllegalArgumentException() {
        proyectoBase.setNombre("   ");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crear(proyectoBase);
        });

        assertEquals("El nombre del proyecto es obligatorio", exception.getMessage());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 8. PRUEBA: Crear proyecto con nombre duplicado
    @Test
    public void crear_ConNombreDuplicado_LanzaIllegalArgumentException() {
        when(proyectoRepository.existsByNombreIgnoreCase(proyectoBase.getNombre())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crear(proyectoBase);
        });

        assertTrue(exception.getMessage().contains("Ya existe un proyecto con el nombre"));
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 9. PRUEBA: Crear proyecto con fechas incoherentes (Sprint 1 nuevo)
    @Test
    public void crear_ConFechasIncoherentes_LanzaIllegalArgumentException() {
        proyectoBase.setFechaInicio(LocalDate.now().plusDays(10));
        proyectoBase.setFechaFin(LocalDate.now()); // Fecha de fin anterior a la de inicio

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crear(proyectoBase);
        });

        assertEquals("La fecha de inicio no puede ser posterior a la fecha de finalización.", exception.getMessage());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 10. PRUEBA: Actualizar proyecto existente
    @Test
    public void actualizar_ConProyectoExistente_GuardaCambiosYEnviaEventoKafka() {
        Proyecto cambios = new Proyecto("Proyecto Beta", "Actualizado", EstadoProyecto.PAUSADO, LocalDate.now(), LocalDate.now().plusDays(5));
        
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoBase));
        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyectoBase); // En el código real, 'proyectoBase' se actualiza por referencia

        Proyecto actualizado = proyectoService.actualizar(1L, cambios);

        assertNotNull(actualizado);
        verify(proyectoRepository, times(1)).save(any(Proyecto.class));

        // Verificamos que se haya enviado el evento de actualización a Kafka
        ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
        verify(kafkaProducerService, times(1)).sendEvent(captor.capture());
        assertEquals("ProjectUpdated", captor.getValue().getEventType());
    }

    // 11. PRUEBA: Actualizar proyecto con fecha de inicio posterior a fecha de fin
    @Test
    public void actualizar_ConFechaInicioPosteriorAFechaFin_LanzaIllegalArgumentException() {
        // Arrange
        Proyecto proyectoExistente = new Proyecto();
        proyectoExistente.setId(1L);
        proyectoExistente.setNombre("Proyecto Alpha");
        proyectoExistente.setEstado(EstadoProyecto.ACTIVO);
        proyectoExistente.setFechaCreacion(LocalDate.of(2024, 1, 1));

        Proyecto cambios = new Proyecto();
        cambios.setNombre("Proyecto Beta");
        cambios.setEstado(EstadoProyecto.ACTIVO);
        cambios.setFechaInicio(LocalDate.of(2025, 12, 31));
        cambios.setFechaFin(LocalDate.of(2025, 1, 1));
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.actualizar(1L, cambios);
        });

        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin", exception.getMessage());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 11. PRUEBA: Actualizar proyecto inexistente
    @Test
    public void actualizar_ProyectoNoExiste_LanzaResourceNotFoundException() {
        Proyecto cambios = new Proyecto("Proyecto Beta", "Desc", EstadoProyecto.ACTIVO, LocalDate.now(), LocalDate.now().plusDays(1));
        when(proyectoRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            proyectoService.actualizar(999L, cambios);
        });

        assertTrue(exception.getMessage().contains("Proyecto no encontrado con ID"));
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 12. PRUEBA: Eliminar proyecto existente
    @Test
    public void eliminar_CuandoExiste_EliminaProyecto() {
        when(proyectoRepository.existsById(1L)).thenReturn(true);

        proyectoService.eliminar(1L);

        verify(proyectoRepository, times(1)).deleteById(1L);
    }

    // 13. PRUEBA: Eliminar proyecto inexistente
    @Test
    public void eliminar_ProyectoNoExiste_LanzaResourceNotFoundException() {
        when(proyectoRepository.existsById(999L)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            proyectoService.eliminar(999L);
        });

        assertTrue(exception.getMessage().contains("Proyecto no encontrado con ID"));
        verify(proyectoRepository, never()).deleteById(anyLong());
    }
}