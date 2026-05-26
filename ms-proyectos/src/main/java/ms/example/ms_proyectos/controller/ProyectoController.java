package ms.example.ms_proyectos.controller;

import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.service.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    // Inyección de dependencias por constructor
    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    /**
     * Obtiene todos los proyectos
     * GET /api/v1/proyectos
     * @return lista de todos los proyectos con estado 200
     */
    @GetMapping
    public ResponseEntity<List<Proyecto>> obtenerTodos() {
        try {
            List<Proyecto> proyectos = proyectoService.obtenerTodos();
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un proyecto por su ID
     * GET /api/v1/proyectos/{id}
     * @param id el ID del proyecto
     * @return el proyecto solicitado o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Optional<Proyecto> proyecto = proyectoService.obtenerPorId(id);
            if (proyecto.isPresent()) {
                return ResponseEntity.ok(proyecto.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("estado", "error");
                error.put("mensaje", "Proyecto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca proyectos por nombre
     * GET /api/v1/proyectos/buscar?nombre=Proyecto
     * @param nombre el nombre a buscar
     * @return lista de proyectos que coinciden
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Proyecto>> buscarPorNombre(@RequestParam String nombre) {
        try {
            List<Proyecto> proyectos = proyectoService.buscarPorNombre(nombre);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene proyectos por estado
     * GET /api/v1/proyectos/estado/{estado}
     * @param estado el estado a filtrar
     * @return lista de proyectos con ese estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Proyecto>> obtenerPorEstado(@PathVariable String estado) {
        try {
            List<Proyecto> proyectos = proyectoService.obtenerPorEstado(estado);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea un nuevo proyecto
     * POST /api/v1/proyectos
     * @param proyecto el proyecto a crear
     * @return el proyecto creado con estado 201
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Proyecto proyecto) {
        try {
            Proyecto proyectoCreado = proyectoService.crear(proyecto);
            return ResponseEntity.status(HttpStatus.CREATED).body(proyectoCreado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("estado", "error");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("estado", "error");
            error.put("mensaje", "Error al crear el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Actualiza un proyecto existente
     * PUT /api/v1/proyectos/{id}
     * @param id el ID del proyecto a actualizar
     * @param proyectoActualizado los datos actualizados
     * @return el proyecto actualizado o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Proyecto proyectoActualizado) {
        try {
            Proyecto proyecto = proyectoService.actualizar(id, proyectoActualizado);
            return ResponseEntity.ok(proyecto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("estado", "error");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("estado", "error");
            error.put("mensaje", "Error al actualizar el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Elimina un proyecto
     * DELETE /api/v1/proyectos/{id}
     * @param id el ID del proyecto a eliminar
     * @return respuesta vacía con estado 204 o 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            proyectoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("estado", "error");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("estado", "error");
            error.put("mensaje", "Error al eliminar el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
