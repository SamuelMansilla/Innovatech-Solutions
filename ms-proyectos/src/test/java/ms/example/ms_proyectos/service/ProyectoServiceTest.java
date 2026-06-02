package ms.example.ms_proyectos.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.repository.ProyectoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private ProyectoService proyectoService;

    // 1. PRUEBA: Obtener todos los proyectos
    @Test
    public void obtenerTodos_DebeRetornarListaDeProyectos() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        when(proyectoRepository.findAll()).thenReturn(List.of(proyecto));

        // Act
        List<Proyecto> result = proyectoService.obtenerTodos();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Proyecto Alpha", result.get(0).getNombre());
        verify(proyectoRepository, times(1)).findAll();
    }

    // 2. PRUEBA: Obtener proyecto por ID existente
    @Test
    public void obtenerPorId_CuandoExiste_RetornaProyecto() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        // Act
        Optional<Proyecto> result = proyectoService.obtenerPorId(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Proyecto Alpha", result.get().getNombre());
        verify(proyectoRepository, times(1)).findById(1L);
    }

    // 3. PRUEBA: Obtener proyecto por ID inexistente
    @Test
    public void obtenerPorId_CuandoNoExiste_RetornaVacio() {
        // Arrange
        when(proyectoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Proyecto> result = proyectoService.obtenerPorId(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(proyectoRepository, times(1)).findById(999L);
    }

    // 4. PRUEBA: Buscar proyecto por nombre
    @Test
    public void buscarPorNombre_RetornaProyectosCoincidentes() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        when(proyectoRepository.findByNombreContainingIgnoreCase("Alpha")).thenReturn(List.of(proyecto));

        // Act
        List<Proyecto> result = proyectoService.buscarPorNombre("Alpha");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Proyecto Alpha", result.get(0).getNombre());
        verify(proyectoRepository, times(1)).findByNombreContainingIgnoreCase("Alpha");
    }

    // 5. PRUEBA: Obtener proyectos por estado
    @Test
    public void obtenerPorEstado_RetornaProyectosDelEstado() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        when(proyectoRepository.findByEstado(EstadoProyecto.ACTIVO)).thenReturn(List.of(proyecto));

        // Act
        List<Proyecto> result = proyectoService.obtenerPorEstado(EstadoProyecto.ACTIVO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(EstadoProyecto.ACTIVO, result.get(0).getEstado());
        verify(proyectoRepository, times(1)).findByEstado(EstadoProyecto.ACTIVO);
    }

    // 6. PRUEBA: Crear proyecto con datos válidos
    @Test
    public void crear_ConDatosValidos_GuardaProyecto() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Nuevo");
        proyecto.setDescripcion("Descripción");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        proyecto.setFechaInicio(LocalDate.of(2025, 1, 1));
        proyecto.setFechaFin(LocalDate.of(2025, 12, 31));

        when(proyectoRepository.existsByNombreIgnoreCase("Proyecto Nuevo")).thenReturn(false);
        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyecto);

        // Act
        Proyecto creado = proyectoService.crear(proyecto);

        // Assert
        assertNotNull(creado);
        assertEquals("Proyecto Nuevo", creado.getNombre());
        assertNotNull(creado.getFechaCreacion());
        assertNotNull(creado.getFechaActualizacion());
        verify(proyectoRepository, times(1)).save(any(Proyecto.class));
    }

    // 7. PRUEBA: Crear proyecto con nombre vacío (validación de negocio)
    @Test
    public void crear_ConNombreVacio_LanzaIllegalArgumentException() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("   ");
        proyecto.setEstado(EstadoProyecto.ACTIVO);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crear(proyecto);
        });

        assertEquals("El nombre del proyecto es obligatorio", exception.getMessage());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 8. PRUEBA: Crear proyecto con nombre duplicado
    @Test
    public void crear_ConNombreDuplicado_LanzaIllegalArgumentException() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);

        when(proyectoRepository.existsByNombreIgnoreCase("Proyecto Alpha")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crear(proyecto);
        });

        assertTrue(exception.getMessage().contains("Ya existe un proyecto con el nombre"));
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 9. PRUEBA: Crear proyecto con fecha de inicio posterior a fecha de fin
    @Test
    public void crear_ConFechaInicioPosteriorAFechaFin_LanzaIllegalArgumentException() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Nuevo");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        proyecto.setFechaInicio(LocalDate.of(2025, 12, 31));
        proyecto.setFechaFin(LocalDate.of(2025, 1, 1));
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crear(proyecto);
        });

        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin", exception.getMessage());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 9. PRUEBA: Crear proyecto sin estado
    @Test
    public void crear_SinEstado_LanzaIllegalArgumentException() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Nuevo");
        proyecto.setEstado(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crear(proyecto);
        });

        assertEquals("El estado del proyecto es obligatorio", exception.getMessage());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 10. PRUEBA: Actualizar proyecto existente
    @Test
    public void actualizar_ConProyectoExistente_GuardaCambios() {
        // Arrange
        Proyecto proyectoExistente = new Proyecto();
        proyectoExistente.setId(1L);
        proyectoExistente.setNombre("Proyecto Alpha");
        proyectoExistente.setEstado(EstadoProyecto.ACTIVO);
        proyectoExistente.setFechaCreacion(LocalDate.of(2024, 1, 1));

        Proyecto cambios = new Proyecto();
        cambios.setNombre("Proyecto Beta");
        cambios.setDescripcion("Actualizado");
        cambios.setEstado(EstadoProyecto.PAUSADO);

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoExistente));
        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyectoExistente);

        // Act
        Proyecto actualizado = proyectoService.actualizar(1L, cambios);

        // Assert
        assertNotNull(actualizado);
        assertEquals("Proyecto Beta", actualizado.getNombre());
        assertEquals(EstadoProyecto.PAUSADO, actualizado.getEstado());
        assertNotNull(actualizado.getFechaActualizacion());
        verify(proyectoRepository, times(1)).save(any(Proyecto.class));
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
    public void actualizar_ProyectoNoExiste_LanzaIllegalArgumentException() {
        // Arrange
        Proyecto cambios = new Proyecto();
        cambios.setNombre("Proyecto Beta");
        cambios.setEstado(EstadoProyecto.ACTIVO);

        when(proyectoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.actualizar(999L, cambios);
        });

        assertTrue(exception.getMessage().contains("Proyecto no encontrado con ID"));
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    // 12. PRUEBA: Eliminar proyecto existente
    @Test
    public void eliminar_CuandoExiste_EliminaProyecto() {
        // Arrange
        when(proyectoRepository.existsById(1L)).thenReturn(true);

        // Act
        proyectoService.eliminar(1L);

        // Assert
        verify(proyectoRepository, times(1)).deleteById(1L);
    }

    // 13. PRUEBA: Eliminar proyecto inexistente
    @Test
    public void eliminar_ProyectoNoExiste_LanzaIllegalArgumentException() {
        // Arrange
        when(proyectoRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.eliminar(999L);
        });

        assertTrue(exception.getMessage().contains("Proyecto no encontrado con ID"));
        verify(proyectoRepository, never()).deleteById(anyLong());
    }
}
