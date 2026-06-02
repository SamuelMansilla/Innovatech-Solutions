package ms.example.ms_proyectos.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de error estandarizadas.
 * Proporciona una estructura consistente para todos los errores de la API.
 */
public class ErrorResponse {

    private int status;
    private String mensaje;
    private LocalDateTime timestamp;
    private String ruta;
    private List<String> detalles;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String mensaje) {
        this();
        this.status = status;
        this.mensaje = mensaje;
    }

    public ErrorResponse(int status, String mensaje, String ruta) {
        this(status, mensaje);
        this.ruta = ruta;
    }

    public ErrorResponse(int status, String mensaje, String ruta, List<String> detalles) {
        this(status, mensaje, ruta);
        this.detalles = detalles;
    }

    // Getters y Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public List<String> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<String> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status=" + status +
                ", mensaje='" + mensaje + '\'' +
                ", timestamp=" + timestamp +
                ", ruta='" + ruta + '\'' +
                ", detalles=" + detalles +
                '}';
    }
}
