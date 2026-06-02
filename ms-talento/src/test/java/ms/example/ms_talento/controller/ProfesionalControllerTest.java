package ms.example.ms_talento.controller;

import ms.example.ms_talento.model.Profesional;
import ms.example.ms_talento.repository.ProfesionalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// ¡NUEVA IMPORTACIÓN MODERNA!
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ProfesionalController.class)
public class ProfesionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ¡NUEVA ANOTACIÓN PARA SPRING BOOT 4+!
    @MockitoBean
    private ProfesionalRepository repository;

    @Test
    public void deberiaObtenerTodosLosProfesionales() throws Exception {
        Profesional p1 = new Profesional("Ana Frontend", "Frontend Developer", 20);
        Profesional p2 = new Profesional("Carlos Cloud", "DevOps Engineer", 0);
        
        Mockito.when(repository.findAll()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/v1/talento/profesionales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Ana Frontend"));
    }

    @Test
    public void deberiaGuardarUnProfesional() throws Exception {
        Profesional profesionalGuardado = new Profesional("Samuel Backend", "Backend Developer", 40);
        profesionalGuardado.setId(1L);

        Mockito.when(repository.save(any(Profesional.class))).thenReturn(profesionalGuardado);

        String jsonPeticion = """
                {
                    "nombre": "Samuel Backend",
                    "rol": "Backend Developer",
                    "horasDisponibles": 40
                }
                """;

        mockMvc.perform(post("/api/v1/talento/profesionales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Samuel Backend"));
    }
}