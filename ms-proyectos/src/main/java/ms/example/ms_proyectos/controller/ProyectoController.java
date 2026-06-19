package ms.example.ms_proyectos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import ms.example.ms_proyectos.dto.ErrorResponse;
import ms.example.ms_proyectos.dto.ProyectoRequest;
import ms.example.ms_proyectos.dto.ProyectoResponse;
import ms.example.ms_proyectos.exception.ResourceNotFoundException;
import ms.example.ms_proyectos.mapper.ProyectoMapper;
import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.service.ProyectoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar proyectos.
 * Endpoints para operaciones CRUD y búsquedas.
 * El manejo de excepciones está centralizado en GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/v1/proyectos")
@Tag(name = "Gestión de Proyectos", description = "Endpoints para crear, buscar, actualizar y eliminar proyectos de Innovatech")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @Operation(summary = "Obtener todos los proyectos", description = "Retorna una lista con todos los proyectos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de proyectos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ProyectoResponse>> obtenerTodos() {
        List<Proyecto> proyectos = proyectoService.obtenerTodos();
        List<ProyectoResponse> respuesta = proyectos.stream()
                .map(ProyectoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Obtener proyecto por ID", description = "Busca un proyecto específico utilizando su identificador único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proyecto encontrado"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponse> obtenerPorId(@PathVariable Long id) {
        return proyectoService.obtenerPorId(id)
                .map(ProyectoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + id));
    }

    @Operation(summary = "Buscar proyectos por nombre", description = "Realiza una búsqueda parcial e insensible a mayúsculas/minúsculas por el nombre del proyecto.")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    @GetMapping("/buscar")
    public ResponseEntity<List<ProyectoResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<Proyecto> proyectos = proyectoService.buscarPorNombre(nombre);
        List<ProyectoResponse> respuesta = proyectos.stream()
                .map(ProyectoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Filtrar proyectos por estado", description = "Obtiene una lista de proyectos que coincidan con el estado especificado (ej. ACTIVO, PLANIFICADO).")
    @ApiResponse(responseCode = "200", description = "Filtro aplicado exitosamente")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProyectoResponse>> obtenerPorEstado(@PathVariable EstadoProyecto estado) {
        List<Proyecto> proyectos = proyectoService.obtenerPorEstado(estado);
        List<ProyectoResponse> respuesta = proyectos.stream()
                .map(ProyectoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Crear un nuevo proyecto", description = "Registra un nuevo proyecto en la base de datos y publica asíncronamente un evento en Kafka.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Proyecto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o violación de reglas de negocio", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest proyectoRequest) {
        Proyecto entidad = ProyectoMapper.toEntity(proyectoRequest);
        Proyecto creado = proyectoService.crear(entidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProyectoMapper.toResponse(creado));
    }

    @Operation(summary = "Actualizar un proyecto existente", description = "Modifica los datos de un proyecto. Verifica reglas de negocio y publica evento en Kafka.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proyecto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio violada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProyectoRequest proyectoRequest) {

        // Limpieza: El ProyectoService ya se encarga de buscar por ID y lanzar error si no existe.
        Proyecto entidad = ProyectoMapper.toEntity(proyectoRequest);
        Proyecto actualizado = proyectoService.actualizar(id, entidad);

        return ResponseEntity.ok(ProyectoMapper.toResponse(actualizado));
    }

    @Operation(summary = "Eliminar un proyecto", description = "Elimina físicamente un proyecto de la base de datos a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proyecto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Limpieza: El ProyectoService ya lanza la excepción si el ID no existe.
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}