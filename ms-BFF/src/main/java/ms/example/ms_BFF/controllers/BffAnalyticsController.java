package ms.example.ms_BFF.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ms.example.ms_BFF.clients.AnaliticaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bff/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class BffAnalyticsController {

    private final AnaliticaClient analiticaClient;

    public BffAnalyticsController(AnaliticaClient analiticaClient) {
        this.analiticaClient = analiticaClient;
    }

    // Usamos el nombre de la instancia configurada en application.properties
    @GetMapping("/report")
    @CircuitBreaker(name = "analiticaService", fallbackMethod = "reportFallback")
    public ResponseEntity<String> getAnalyticsReport(@RequestParam(name = "type") String type) {
        // El BFF llama al microservicio real
        String report = analiticaClient.getReport(type);
        return ResponseEntity.ok(report);
    }

    // EL FALLBACK: Si ms-analitica falla o tarda mucho, se ejecuta esto
    public ResponseEntity<String> reportFallback(String type, Throwable ex) {
        String errorResponse = String.format(
            "{\"status\": \"degraded\", \"message\": \"El servicio de analítica está temporalmente fuera de línea. No se pudo generar el reporte para %s. Por favor, intente más tarde.\"}", 
            type
        );
        return ResponseEntity.status(503).body(errorResponse);
    }
}