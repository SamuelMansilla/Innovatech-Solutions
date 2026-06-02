package ms.example.ms_talento.controller;

import ms.example.ms_talento.model.Profesional;
import ms.example.ms_talento.repository.ProfesionalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/talento/profesionales")
public class ProfesionalController {

    private final ProfesionalRepository repository;

    // Inyección de dependencias por constructor
    public ProfesionalController(ProfesionalRepository repository) {
        this.repository = repository;
    }

    // Endpoint para guardar un nuevo profesional
    @PostMapping
    public ResponseEntity<Profesional> registrarProfesional(@RequestBody Profesional profesional) {
        Profesional guardado = repository.save(profesional);
        return ResponseEntity.ok(guardado);
    }

    // Endpoint para obtener todos los profesionales
    @GetMapping
    public ResponseEntity<List<Profesional>> obtenerTodos() {
        return ResponseEntity.ok(repository.findAll());
    }
}