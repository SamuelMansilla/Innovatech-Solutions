package ms.example.ms_proyectos.controller;

import jakarta.validation.Valid;
import ms.example.ms_proyectos.dto.ProyectoRequest;
import ms.example.ms_proyectos.dto.ProyectoResponse;
import ms.example.ms_proyectos.mapper.ProyectoMapper;
import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.service.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public ResponseEntity<List<ProyectoResponse>> obtenerTodos() {
        try {
            List<Proyecto> proyectos = proyectoService.obtenerTodos();
            List<ProyectoResponse> resp = proyectos.stream().map(ProyectoMapper::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return proyectoService.obtenerPorId(id)
                    .map(ProyectoMapper::toResponse)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("estado", "error", "mensaje", "Proyecto no encontrado con ID: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProyectoResponse>> buscarPorNombre(@RequestParam String nombre) {
        try {
            List<Proyecto> proyectos = proyectoService.buscarPorNombre(nombre);
            List<ProyectoResponse> resp = proyectos.stream().map(ProyectoMapper::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProyectoResponse>> obtenerPorEstado(@PathVariable EstadoProyecto estado) {
        try {
            List<Proyecto> proyectos = proyectoService.obtenerPorEstado(estado);
            List<ProyectoResponse> resp = proyectos.stream().map(ProyectoMapper::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProyectoRequest proyectoRequest) {
        try {
            Proyecto entidad = ProyectoMapper.toEntity(proyectoRequest);
            Proyecto creado = proyectoService.crear(entidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(ProyectoMapper.toResponse(creado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("estado", "error", "mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("estado", "error", "mensaje", "Error al crear el proyecto"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody ProyectoRequest proyectoRequest) {
        try {
            Proyecto proyectoActualizado = proyectoService.obtenerPorId(id).orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + id));
            ProyectoMapper.updateEntityFromRequest(proyectoRequest, proyectoActualizado);
            Proyecto guardado = proyectoService.actualizar(id, proyectoActualizado);
            return ResponseEntity.ok(ProyectoMapper.toResponse(guardado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("estado", "error", "mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("estado", "error", "mensaje", "Error al actualizar el proyecto"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            proyectoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("estado", "error", "mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("estado", "error", "mensaje", "Error al eliminar el proyecto"));
        }
    }
}
