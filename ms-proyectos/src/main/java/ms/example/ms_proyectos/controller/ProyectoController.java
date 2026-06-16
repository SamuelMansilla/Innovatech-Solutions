package ms.example.ms_proyectos.controller;

import jakarta.validation.Valid;
import ms.example.ms_proyectos.dto.ProyectoRequest;
import ms.example.ms_proyectos.dto.ProyectoResponse;
import ms.example.ms_proyectos.mapper.ProyectoMapper;
import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.service.ProyectoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar proyectos.
 * Endpoints para operaciones CRUD y búsquedas.
 * El manejo de excepciones está centralizado en GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/v1/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    /**
     * GET /api/v1/proyectos
     * Obtiene todos los proyectos.
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EstadoProyecto estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicioDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFinHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(direction, sortField));

        Page<Proyecto> pageResult = proyectoService.obtenerConFiltros(nombre, estado, fechaInicioDesde, fechaFinHasta, pageable);
        Page<ProyectoResponse> respuesta = pageResult.map(ProyectoMapper::toResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(pageResult.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(pageResult.getTotalPages()));
        headers.add("X-Page-Number", String.valueOf(pageResult.getNumber()));
        headers.add("X-Page-Size", String.valueOf(pageResult.getSize()));

        StringBuilder link = new StringBuilder();
        if (pageResult.hasPrevious()) {
            link.append(String.format("</api/v1/proyectos?page=%d&size=%d&sort=%s>; rel=\"prev\",", page - 1, size, sort));
        }
        if (pageResult.hasNext()) {
            link.append(String.format("</api/v1/proyectos?page=%d&size=%d&sort=%s>; rel=\"next\",", page + 1, size, sort));
        }
        if (link.length() > 0) {
            headers.add(HttpHeaders.LINK, link.toString().replaceAll(",$", ""));
        }

        return ResponseEntity.ok().headers(headers).body(respuesta);
    }

    /**
     * Compatibilidad: versión sin parámetros (usada por tests antiguos).
     */
    public ResponseEntity<List<ProyectoResponse>> obtenerTodos() {
        List<Proyecto> proyectos = proyectoService.obtenerTodos();
        List<ProyectoResponse> respuesta = proyectos.stream()
                .map(ProyectoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * GET /api/v1/proyectos/{id}
     * Obtiene un proyecto por ID.
     * @throws GlobalExceptionHandler.ResourceNotFoundException si el proyecto no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponse> obtenerPorId(@PathVariable Long id) {
        return proyectoService.obtenerPorId(id)
                .map(ProyectoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException(
                        "Proyecto no encontrado con ID: " + id));
    }

    /**
     * GET /api/v1/proyectos/buscar?nombre=xxx
     * Busca proyectos por nombre (búsqueda parcial, insensible a mayúsculas).
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProyectoResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<Proyecto> proyectos = proyectoService.buscarPorNombre(nombre);
        List<ProyectoResponse> respuesta = proyectos.stream()
                .map(ProyectoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * GET /api/v1/proyectos/estado/{estado}
     * Obtiene proyectos filtrados por estado.
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProyectoResponse>> obtenerPorEstado(@PathVariable EstadoProyecto estado) {
        List<Proyecto> proyectos = proyectoService.obtenerPorEstado(estado);
        List<ProyectoResponse> respuesta = proyectos.stream()
                .map(ProyectoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * POST /api/v1/proyectos
     * Crea un nuevo proyecto.
     * @throws IllegalArgumentException si los datos son inválidos o el nombre está duplicado
     */
    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest proyectoRequest) {
        Proyecto entidad = ProyectoMapper.toEntity(proyectoRequest);
        Proyecto creado = proyectoService.crear(entidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProyectoMapper.toResponse(creado));
    }

    /**
     * PUT /api/v1/proyectos/{id}
     * Actualiza un proyecto existente.
     * @throws GlobalExceptionHandler.ResourceNotFoundException si el proyecto no existe
     * @throws IllegalArgumentException si los datos son inválidos
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProyectoRequest proyectoRequest) {

        Proyecto proyectoExistente = proyectoService.obtenerPorId(id)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException(
                        "Proyecto no encontrado con ID: " + id));

        ProyectoMapper.updateEntityFromRequest(proyectoRequest, proyectoExistente);
        Proyecto actualizado = proyectoService.actualizar(id, proyectoExistente);

        return ResponseEntity.ok(ProyectoMapper.toResponse(actualizado));
    }

    /**
     * DELETE /api/v1/proyectos/{id}
     * Elimina un proyecto.
     * @throws GlobalExceptionHandler.ResourceNotFoundException si el proyecto no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!proyectoService.obtenerPorId(id).isPresent()) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(
                    "Proyecto no encontrado con ID: " + id);
        }
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
