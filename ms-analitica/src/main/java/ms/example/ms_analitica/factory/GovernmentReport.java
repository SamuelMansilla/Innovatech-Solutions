package ms.example.ms_analitica.factory;

public class GovernmentReport implements IReport {
    @Override
    public String generate() {
        return "{\"sector\": \"Gobierno\", \"estado\": \"OK\", \"datos\": \"Reporte de auditoría de fondos y cumplimiento regulatorio\"}";
    }
}