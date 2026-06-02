package ms.example.ms_proyectos.controller;

import static org.junit.jupiter.api.Assertions.*;

import ms.example.ms_proyectos.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Tests para verificar que GlobalExceptionHandler maneja excepciones correctamente.
 */
@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    // 1. PRUEBA: Manejo de ResourceNotFoundException
    @Test
    public void handleResourceNotFoundException_RetornaError404() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/proyectos/999");
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);

        GlobalExceptionHandler.ResourceNotFoundException ex =
                new GlobalExceptionHandler.ResourceNotFoundException("Proyecto no encontrado con ID: 999");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, servletWebRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Proyecto no encontrado con ID: 999", response.getBody().getMensaje());
        assertNotNull(response.getBody().getTimestamp());
    }

    // 2. PRUEBA: Manejo de IllegalArgumentException
    @Test
    public void handleIllegalArgumentException_RetornaError400() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/proyectos");
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);

        IllegalArgumentException ex = new IllegalArgumentException("El nombre del proyecto es obligatorio");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, servletWebRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("El nombre del proyecto es obligatorio", response.getBody().getMensaje());
    }

    // 3. PRUEBA: Manejo de excepciones genéricas (Exception)
    @Test
    public void handleGlobalException_RetornaError500() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/proyectos");
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);

        Exception ex = new RuntimeException("Error inesperado en la base de datos");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, servletWebRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMensaje().contains("Error interno del servidor"));
    }

    // 4. PRUEBA: ErrorResponse tiene timestamp y ruta
    @Test
    public void errorResponse_TieneTimestampYRuta() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/proyectos/123");
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);

        GlobalExceptionHandler.ResourceNotFoundException ex =
                new GlobalExceptionHandler.ResourceNotFoundException("Test error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, servletWebRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getRuta());
        assertTrue(response.getBody().getRuta().contains("/api/v1/proyectos/123"));
    }
}
