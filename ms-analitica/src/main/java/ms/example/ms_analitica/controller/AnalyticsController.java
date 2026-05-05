package main.java.ms.example.ms_analitica.controller;

import ms.example.ms_analitica.factory.AnalyticsFactory;
import ms.example.ms_analitica.factory.IReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsFactory analyticsFactory;

    // Inyección de dependencias mediante constructor (Mejor práctica en Spring Boot)
    public AnalyticsController(AnalyticsFactory analyticsFactory) {
        this.analyticsFactory = analyticsFactory;
    }

    @GetMapping("/report")
    public ResponseEntity<String> getReport(@RequestParam(name = "type") String type) {
        try {
            // El Factory Method decide qué instancia crear basándose en el parámetro "type"
            IReport report = analyticsFactory.createReport(type);
            
            // Retorna el JSON generado con código HTTP 200 OK
            return ResponseEntity.ok(report.generate());
            
        } catch (IllegalArgumentException e) {
            // Retorna un JSON de error estructurado con código HTTP 400 Bad Request
            String errorJson = String.format("{\"status\": \"error\", \"message\": \"%s\"}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorJson);
        } catch (Exception e) {
            // Manejo de errores genéricos con código HTTP 500 Internal Server Error
            String serverErrorJson = "{\"status\": \"error\", \"message\": \"Error interno en el servidor de analítica\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serverErrorJson);
        }
    }
}