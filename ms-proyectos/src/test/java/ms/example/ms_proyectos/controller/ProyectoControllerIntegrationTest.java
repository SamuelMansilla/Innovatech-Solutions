package ms.example.ms_proyectos.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.service.ProyectoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Tests de lógica del controlador sin necesidad de MockMvc o Spring Test.
 * Se verifica que el controlador delegue correctamente al servicio
 * y retorne los códigos HTTP apropiados.
 */
@ExtendWith(MockitoExtension.class)
public class ProyectoControllerIntegrationTest {

    @Mock
    private ProyectoService proyectoService;

    @InjectMocks
    private ProyectoController proyectoController;

    // 1. PRUEBA: Obtener todos los proyectos
    @Test
    public void obtenerTodos_DebeRetornarListaDeProyectos() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        when(proyectoService.obtenerTodos()).thenReturn(List.of(proyecto));

        // Act
        ResponseEntity<?> response = proyectoController.obtenerTodos();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(proyectoService, times(1)).obtenerTodos();
    }

    // 2. PRUEBA: Obtener proyecto por ID existente
    @Test
    public void obtenerPorId_CuandoExiste_Retorna200() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        when(proyectoService.obtenerPorId(1L)).thenReturn(Optional.of(proyecto));

        // Act
        ResponseEntity<?> response = proyectoController.obtenerPorId(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(proyectoService, times(1)).obtenerPorId(1L);
    }

    // 3. PRUEBA: Obtener proyecto por ID inexistente
    @Test
    public void obtenerPorId_CuandoNoExiste_Retorna404() {
        // Arrange
        when(proyectoService.obtenerPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GlobalExceptionHandler.ResourceNotFoundException.class, () -> {
            proyectoController.obtenerPorId(999L);
        });

        verify(proyectoService, times(1)).obtenerPorId(999L);
    }

    // 4. PRUEBA: Buscar proyecto por nombre
    @Test
    public void buscarPorNombre_RetornaProyectosCoincidentes() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        when(proyectoService.buscarPorNombre("Alpha")).thenReturn(List.of(proyecto));

        // Act
        ResponseEntity<?> response = proyectoController.buscarPorNombre("Alpha");

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(proyectoService, times(1)).buscarPorNombre("Alpha");
    }

    // 5. PRUEBA: Obtener proyectos por estado
    @Test
    public void obtenerPorEstado_RetornaProyectosDelEstado() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        when(proyectoService.obtenerPorEstado(EstadoProyecto.ACTIVO)).thenReturn(List.of(proyecto));

        // Act
        ResponseEntity<?> response = proyectoController.obtenerPorEstado(EstadoProyecto.ACTIVO);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(proyectoService, times(1)).obtenerPorEstado(EstadoProyecto.ACTIVO);
    }

    // 6. PRUEBA: Crear proyecto con datos válidos
    @Test
    public void crear_ConDatosValidos_Retorna201() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(2L);
        proyecto.setNombre("Proyecto Nuevo");
        proyecto.setDescripcion("Descripción nueva");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        proyecto.setFechaInicio(LocalDate.of(2025, 2, 1));
        proyecto.setFechaFin(LocalDate.of(2025, 11, 30));
        proyecto.setFechaCreacion(LocalDate.now());
        proyecto.setFechaActualizacion(LocalDate.now());

        when(proyectoService.crear(any(Proyecto.class))).thenReturn(proyecto);

        // Act
        ResponseEntity<?> response = proyectoController.crear(new ms.example.ms_proyectos.dto.ProyectoRequest());

        // Assert
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(proyectoService, times(1)).crear(any(Proyecto.class));
    }

    // 7. PRUEBA: Crear proyecto con nombre vacío (validación)
    @Test
    public void crear_ConNombreVacio_LanzaExcepcion() {
        // Arrange
        when(proyectoService.crear(any(Proyecto.class)))
                .thenThrow(new IllegalArgumentException("El nombre del proyecto es obligatorio"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            proyectoController.crear(new ms.example.ms_proyectos.dto.ProyectoRequest());
        });
    }

    // 8. PRUEBA: Actualizar proyecto existente
    @Test
    public void actualizar_ConProyectoExistente_Retorna200() {
        // Arrange
        Proyecto proyectoExistente = new Proyecto();
        proyectoExistente.setId(1L);
        proyectoExistente.setNombre("Proyecto Alpha");
        proyectoExistente.setEstado(EstadoProyecto.ACTIVO);

        Proyecto actualizado = new Proyecto();
        actualizado.setId(1L);
        actualizado.setNombre("Proyecto Beta");
        actualizado.setEstado(EstadoProyecto.PAUSADO);
        actualizado.setFechaActualizacion(LocalDate.now());

        when(proyectoService.obtenerPorId(1L)).thenReturn(Optional.of(proyectoExistente));
        when(proyectoService.actualizar(eq(1L), any(Proyecto.class))).thenReturn(actualizado);

        // Act
        ResponseEntity<?> response = proyectoController.actualizar(1L, new ms.example.ms_proyectos.dto.ProyectoRequest());

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(proyectoService, times(1)).obtenerPorId(1L);
        verify(proyectoService, times(1)).actualizar(eq(1L), any(Proyecto.class));
    }

    // 9. PRUEBA: Actualizar proyecto inexistente
    @Test
    public void actualizar_ProyectoNoExiste_LanzaExcepcion() {
        // Arrange
        when(proyectoService.obtenerPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GlobalExceptionHandler.ResourceNotFoundException.class, () -> {
            proyectoController.actualizar(999L, new ms.example.ms_proyectos.dto.ProyectoRequest());
        });

        verify(proyectoService, times(1)).obtenerPorId(999L);
        verify(proyectoService, never()).actualizar(anyLong(), any(Proyecto.class));
    }

    // 10. PRUEBA: Eliminar proyecto existente
    @Test
    public void eliminar_CuandoExiste_Retorna204() {
        // Arrange
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setEstado(EstadoProyecto.ACTIVO);

        when(proyectoService.obtenerPorId(1L)).thenReturn(Optional.of(proyecto));
        doNothing().when(proyectoService).eliminar(1L);

        // Act
        ResponseEntity<Void> response = proyectoController.eliminar(1L);

        // Assert
        assertEquals(204, response.getStatusCode().value());
        verify(proyectoService, times(1)).obtenerPorId(1L);
        verify(proyectoService, times(1)).eliminar(1L);
    }

    // 11. PRUEBA: Eliminar proyecto inexistente
    @Test
    public void eliminar_ProyectoNoExiste_LanzaExcepcion() {
        // Arrange
        when(proyectoService.obtenerPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GlobalExceptionHandler.ResourceNotFoundException.class, () -> {
            proyectoController.eliminar(999L);
        });

        verify(proyectoService, times(1)).obtenerPorId(999L);
        verify(proyectoService, never()).eliminar(anyLong());
    }
}
