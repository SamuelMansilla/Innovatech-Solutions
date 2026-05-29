package ms.example.ms_proyectos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.service.ProyectoService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProyectoController.class)
class ProyectoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProyectoService proyectoService;

    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setDescripcion("Descripción de prueba");
        proyecto.setEstado(EstadoProyecto.ACTIVO);
        proyecto.setFechaInicio(LocalDate.of(2025, 1, 1));
        proyecto.setFechaFin(LocalDate.of(2025, 12, 31));
        proyecto.setFechaCreacion(LocalDate.of(2024, 10, 1));
        proyecto.setFechaActualizacion(LocalDate.of(2025, 1, 1));
    }

    @Test
    void obtenerTodos_debeRetornarListaDeProyectos() throws Exception {
        given(proyectoService.obtenerTodos()).willReturn(List.of(proyecto));

        mockMvc.perform(get("/api/v1/proyectos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Proyecto Alpha"))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    void obtenerPorId_cuandoExiste_retornaProyecto() throws Exception {
        given(proyectoService.obtenerPorId(1L)).willReturn(Optional.of(proyecto));

        mockMvc.perform(get("/api/v1/proyectos/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Proyecto Alpha"));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_retorna404() throws Exception {
        given(proyectoService.obtenerPorId(1L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/proyectos/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Proyecto no encontrado con ID: 1"));
    }

    @Test
    void buscarPorNombre_retornaProyectosMatching() throws Exception {
        given(proyectoService.buscarPorNombre("Alpha")).willReturn(List.of(proyecto));

        mockMvc.perform(get("/api/v1/proyectos/buscar").param("nombre", "Alpha").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Proyecto Alpha"));
    }

    @Test
    void obtenerPorEstado_retornaProyectosPorEstado() throws Exception {
        given(proyectoService.obtenerPorEstado(EstadoProyecto.ACTIVO)).willReturn(List.of(proyecto));

        mockMvc.perform(get("/api/v1/proyectos/estado/ACTIVO").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    void crear_conRequestValido_retorna201() throws Exception {
        var request = new java.util.HashMap<String, Object>();
        request.put("nombre", "Proyecto Nuevo");
        request.put("descripcion", "Descripción nueva");
        request.put("estado", "ACTIVO");
        request.put("fechaInicio", "2025-02-01");
        request.put("fechaFin", "2025-11-30");

        var created = new Proyecto();
        created.setId(2L);
        created.setNombre("Proyecto Nuevo");
        created.setDescripcion("Descripción nueva");
        created.setEstado(EstadoProyecto.ACTIVO);
        created.setFechaInicio(LocalDate.of(2025, 2, 1));
        created.setFechaFin(LocalDate.of(2025, 11, 30));
        created.setFechaCreacion(LocalDate.of(2025, 2, 1));
        created.setFechaActualizacion(LocalDate.of(2025, 2, 1));

        given(proyectoService.crear(any(Proyecto.class))).willReturn(created);

        mockMvc.perform(post("/api/v1/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nombre").value("Proyecto Nuevo"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void actualizar_conRequestValido_retorna200() throws Exception {
        var update = new java.util.HashMap<String, Object>();
        update.put("nombre", "Proyecto Actualizado");
        update.put("descripcion", "Descripción actualizada");
        update.put("estado", "PAUSADO");
        update.put("fechaInicio", "2025-03-01");
        update.put("fechaFin", "2025-10-31");

        var updated = new Proyecto();
        updated.setId(1L);
        updated.setNombre("Proyecto Actualizado");
        updated.setDescripcion("Descripción actualizada");
        updated.setEstado(EstadoProyecto.PAUSADO);
        updated.setFechaInicio(LocalDate.of(2025, 3, 1));
        updated.setFechaFin(LocalDate.of(2025, 10, 31));
        updated.setFechaCreacion(LocalDate.of(2024, 10, 1));
        updated.setFechaActualizacion(LocalDate.of(2025, 3, 1));

        given(proyectoService.obtenerPorId(1L)).willReturn(Optional.of(proyecto));
        given(proyectoService.actualizar(eq(1L), any(Proyecto.class))).willReturn(updated);

        mockMvc.perform(put("/api/v1/proyectos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proyecto Actualizado"))
                .andExpect(jsonPath("$.estado").value("PAUSADO"));
    }

    @Test
    void eliminar_cuandoExiste_retorna204() throws Exception {
        doNothing().when(proyectoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/proyectos/1"))
                .andExpect(status().isNoContent());
    }
}
