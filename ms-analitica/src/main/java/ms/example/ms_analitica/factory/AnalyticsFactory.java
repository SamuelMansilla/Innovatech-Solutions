package main.java.ms.example.ms_analitica.factory;

import org.springframework.stereotype.Component;

@Component
public class AnalyticsFactory extends ReportFactory {

    @Override
    public IReport createReport(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de reporte no puede estar vacío");
        }

        return switch (type.toLowerCase()) {
            case "fintech" -> new FintechReport();
            case "retail" -> new RetailReport();
            case "gobierno" -> new GovernmentReport();
            default -> throw new IllegalArgumentException("Tipo de reporte no soportado en Innovatech: " + type);
        };
    }
}