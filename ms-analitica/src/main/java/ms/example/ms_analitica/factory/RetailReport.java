package main.java.ms.example.ms_analitica.factory;

public class RetailReport implements IReport {
    @Override
    public String generate() {
        return "{\"sector\": \"Retail\", \"estado\": \"OK\", \"datos\": \"Métricas de volumen de ventas y rotación de inventario generadas\"}";
    }
}