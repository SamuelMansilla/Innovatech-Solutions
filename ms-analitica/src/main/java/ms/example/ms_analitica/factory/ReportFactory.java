package main.java.ms.example.ms_analitica.factory;

import org.springframework.stereotype.Component;

// Interfaz Abstracta de la Fábrica[cite: 2]
public interface ReportFactory {
    IReport createReport(String type);
}

// Fábrica Concreta[cite: 2]
@Component
public class AnalyticsFactory implements ReportFactory {
    
    @Override
    public IReport createReport(String type) {
        if (type == null) {
            return null;
        }
        return switch (type.toLowerCase()) {
            case "fintech" -> new FintechReport();
            case "retail" -> new RetailReport();
            case "gobierno" -> new GovernmentReport();
            default -> throw new IllegalArgumentException("Tipo de reporte no soportado: " + type);
        };
    }
}