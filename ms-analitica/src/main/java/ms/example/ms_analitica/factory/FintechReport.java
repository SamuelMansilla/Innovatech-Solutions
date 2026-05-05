package main.java.ms.example.ms_analitica.factory;

public class FintechReport implements IReport {
    @Override
    public String generate() {
        // En un caso real, aquí iría la lógica para consultar métricas de la base de datos
        return "{\"sector\": \"Fintech\", \"estado\": \"OK\", \"datos\": \"Análisis de riesgo y transacciones cifradas completado\"}";
    }
}