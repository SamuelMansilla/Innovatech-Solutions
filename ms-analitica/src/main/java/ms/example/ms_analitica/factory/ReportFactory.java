package ms.example.ms_analitica.factory;

public abstract class ReportFactory {
    public abstract IReport createReport(String type);
}