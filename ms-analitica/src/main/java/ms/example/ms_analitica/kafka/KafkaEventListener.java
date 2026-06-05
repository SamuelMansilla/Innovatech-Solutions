package ms.example.ms_analitica.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ms.example.ms_analitica.events.ProjectEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventListener {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final AnalyticsEventStore store;

    public KafkaEventListener(AnalyticsEventStore store) {
        this.store = store;
    }

    @KafkaListener(topics = "projects.events", groupId = "analitica-group")
    public void handle(String message) {
        try {
            ProjectEvent event = objectMapper.readValue(message, ProjectEvent.class);
            // Por ahora guardamos en memoria para pruebas y posterior procesamiento
            store.add(event);
        } catch (Exception e) {
            // manejar error de parsing/logging; no se propaga para evitar reintentos infinitos
            System.err.println("Failed to parse project event: " + e.getMessage());
        }
    }
}
