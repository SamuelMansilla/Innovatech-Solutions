package ms.example.ms_analitica.kafka;

import ms.example.ms_analitica.events.ProjectEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AnalyticsEventStore {
    private final List<ProjectEvent> events = Collections.synchronizedList(new ArrayList<>());

    public void add(ProjectEvent e) {
        events.add(e);
    }

    public List<ProjectEvent> all() { return new ArrayList<>(events); }

    public void clear() { events.clear(); }
}
