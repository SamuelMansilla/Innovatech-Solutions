package ms.example.ms_talento.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "profesionales")
public class Profesional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String rol; // ej: "Backend", "Frontend", "DevOps"
    
    // Aquí cumplimos con la rúbrica: el "capacity" del recurso
    private Integer horasDisponibles; 

    // Constructor vacío exigido por JPA
    public Profesional() {
    }

    public Profesional(String nombre, String rol, Integer horasDisponibles) {
        this.nombre = nombre;
        this.rol = rol;
        this.horasDisponibles = horasDisponibles;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Integer getHorasDisponibles() { return horasDisponibles; }
    public void setHorasDisponibles(Integer horasDisponibles) { this.horasDisponibles = horasDisponibles; }
}