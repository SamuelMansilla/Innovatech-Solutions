package ms.example.ms_analitica.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ms.example.common.events.ProjectEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaEventListenerTest {

    @Test
    void listenerParsesAndStoresEvent() throws Exception {
        AnalyticsEventStore store = new AnalyticsEventStore();
        KafkaEventListener listener = new KafkaEventListener(store);

        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
        ProjectEvent e = new ProjectEvent();
        e.setEventType("ProjectCreated");
        e.setId(10L);
        e.setNombre("Prueba");
        e.setEstado("ACTIVO");
        e.setFechaCreacion(LocalDate.now());
        String json = om.writeValueAsString(e);

        // Llamamos directamente al manejador (sin broker embebido ni contexto Spring)
        listener.handle(json);

        assertThat(store.all()).hasSize(1);
        ProjectEvent stored = store.all().get(0);
        assertThat(stored.getId()).isEqualTo(10L);
        assertThat(stored.getEventType()).isEqualTo("ProjectCreated");
    }
}
