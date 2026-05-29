package ms.example.ms_proyectos.service;

import ms.example.ms_proyectos.model.EstadoProyecto;
import ms.example.ms_proyectos.model.Proyecto;
import ms.example.ms_proyectos.repository.ProyectoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProyectoServiceTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @InjectMocks
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
    }

    @Test
    void obtenerTodos_debeRetornarListaDeProyectos() {
        given(proyectoRepository.findAll()).willReturn(List.of(proyecto));

        List<Proyecto> result = proyectoService.obtenerTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Proyecto Alpha");
    }

    @Test
    void obtenerPorId_cuandoExiste_retornaOptionalConProyecto() {
        given(proyectoRepository.findById(1L)).willReturn(Optional.of(proyecto));

        Optional<Proyecto> result = proyectoService.obtenerPorId(1L);

        assertThat(result).isPresent();
        assertThat(result).contains(proyecto);
    }

    @Test
    void buscarPorNombre_debeDelegarAlRepositorio() {
        given(proyectoRepository.findByNombreContainingIgnoreCase("Alpha")).willReturn(List.of(proyecto));

        List<Proyecto> result = proyectoService.buscarPorNombre("Alpha");

        assertThat(result).singleElement().isEqualTo(proyecto);
    }

    @Test
    void obtenerPorEstado_debeDelegarAlRepositorio() {
        given(proyectoRepository.findByEstado(EstadoProyecto.ACTIVO)).willReturn(List.of(proyecto));

        List<Proyecto> result = proyectoService.obtenerPorEstado(EstadoProyecto.ACTIVO);

        assertThat(result).singleElement().isEqualTo(proyecto);
    }

    @Test
    void crear_conNombreVacio_lanzaIllegalArgumentException() {
        Proyecto invalid = new Proyecto();
        invalid.setNombre("   ");
        invalid.setEstado(EstadoProyecto.ACTIVO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> proyectoService.crear(invalid));

        assertThat(exception).hasMessageContaining("nombre del proyecto es obligatorio");
    }

    @Test
    void crear_conNombreDuplicado_lanzaIllegalArgumentException() {
        given(proyectoRepository.existsByNombreIgnoreCase("Proyecto Alpha")).willReturn(true);
        Proyecto nuevo = new Proyecto();
        nuevo.setNombre("Proyecto Alpha");
        nuevo.setEstado(EstadoProyecto.ACTIVO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> proyectoService.crear(nuevo));

        assertThat(exception.getMessage()).contains("Ya existe un proyecto con el nombre");
    }

    @Test
    void crear_conDatosValidos_guardaProyectoConFechas() {
        given(proyectoRepository.existsByNombreIgnoreCase(any())).willReturn(false);
        given(proyectoRepository.save(any(Proyecto.class))).willAnswer(invocation -> invocation.getArgument(0));

        Proyecto creado = proyectoService.crear(proyecto);

        assertThat(creado.getFechaCreacion()).isNotNull();
        assertThat(creado.getFechaActualizacion()).isNotNull();
        then(proyectoRepository).should(times(1)).save(any(Proyecto.class));
    }

    @Test
    void actualizar_siNoExiste_lanzaIllegalArgumentException() {
        given(proyectoRepository.findById(anyLong())).willReturn(Optional.empty());

        Proyecto request = new Proyecto();
        request.setNombre("Nuevo Nombre");
        request.setEstado(EstadoProyecto.ACTIVO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> proyectoService.actualizar(1L, request));

        assertThat(exception.getMessage()).contains("Proyecto no encontrado con ID");
    }

    @Test
    void actualizar_conProyectoExistente_guardaCambios() {
        given(proyectoRepository.findById(1L)).willReturn(Optional.of(proyecto));
        given(proyectoRepository.save(any(Proyecto.class))).willAnswer(invocation -> invocation.getArgument(0));

        Proyecto cambios = new Proyecto();
        cambios.setNombre("Proyecto Beta");
        cambios.setDescripcion("Actualizado");
        cambios.setEstado(EstadoProyecto.PAUSADO);
        cambios.setFechaInicio(LocalDate.of(2025, 2, 1));
        cambios.setFechaFin(LocalDate.of(2025, 11, 30));

        Proyecto actualizado = proyectoService.actualizar(1L, cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Proyecto Beta");
        assertThat(actualizado.getEstado()).isEqualTo(EstadoProyecto.PAUSADO);
        assertThat(actualizado.getFechaActualizacion()).isNotNull();
        then(proyectoRepository).should(times(1)).save(proyecto);
    }

    @Test
    void eliminar_siNoExiste_lanzaIllegalArgumentException() {
        given(proyectoRepository.existsById(1L)).willReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> proyectoService.eliminar(1L));

        assertThat(exception.getMessage()).contains("Proyecto no encontrado con ID");
    }

    @Test
    void eliminar_cuandoExiste_eliminaProyecto() {
        given(proyectoRepository.existsById(1L)).willReturn(true);

        proyectoService.eliminar(1L);

        then(proyectoRepository).should(times(1)).deleteById(1L);
    }
}
