package ms.example.ms_talento.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProfesionalTest {

    @Test
    public void testConstructorVacioYGettersSetters() {
        // Test del constructor obligatorio para JPA
        Profesional profesional = new Profesional();
        
        // Test de Setters
        profesional.setId(10L);
        profesional.setNombre("Francisco DevOps");
        profesional.setRol("DevOps Engineer");
        profesional.setHorasDisponibles(30);

        // Test de Getters
        assertEquals(10L, profesional.getId());
        assertEquals("Francisco DevOps", profesional.getNombre());
        assertEquals("DevOps Engineer", profesional.getRol());
        assertEquals(30, profesional.getHorasDisponibles());
    }

    @Test
    public void testConstructorConParametros() {
        // Test del constructor parametrizado
        Profesional profesional = new Profesional("Hans Fullstack", "Fullstack Developer", 45);

        assertNull(profesional.getId()); // El ID se genera en BD, debe iniciar null
        assertEquals("Hans Fullstack", profesional.getNombre());
        assertEquals("Fullstack Developer", profesional.getRol());
        assertEquals(45, profesional.getHorasDisponibles());
    }
}