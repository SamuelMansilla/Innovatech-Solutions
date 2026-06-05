package ms.example.ms_analitica.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ms.example.ms_analitica.events.ProjectEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.DockerClientFactory;
import org.junit.jupiter.api.Assumptions;


import java.time.LocalDate;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaListenerIT {

    @Test
    void listenerConsumesMessageFromTopic() throws Exception {
        Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker not available, skipping Kafka IT");

        KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.1"));
        kafka.start();

        try {
            ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
            ProjectEvent e = new ProjectEvent();
            e.setEventType("ProjectCreated");
            e.setId(42L);
            e.setNombre("IT-Test");
            e.setEstado("ACTIVO");
            e.setFechaCreacion(LocalDate.now());
            String json = om.writeValueAsString(e);

            Properties producerProps = new Properties();
            producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            try (KafkaProducer<String,String> producer = new KafkaProducer<>(producerProps)) {
                producer.send(new ProducerRecord<>("projects.events", json)).get();
            }

            // Create a consumer to read the message
            java.util.Properties consumerProps = new java.util.Properties();
            consumerProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            consumerProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "it-group-analitica");
            consumerProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
            consumerProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
            consumerProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            try (org.apache.kafka.clients.consumer.KafkaConsumer<String,String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(consumerProps)) {
                consumer.subscribe(java.util.Collections.singletonList("projects.events"));

                org.apache.kafka.clients.consumer.ConsumerRecord<String,String> record = null;
                long deadline = System.currentTimeMillis() + 10000;
                while (System.currentTimeMillis() < deadline) {
                    var recs = consumer.poll(java.time.Duration.ofMillis(500));
                    if (!recs.isEmpty()) { record = recs.iterator().next(); break; }
                }

                // Now simulate the listener processing the received message
                AnalyticsEventStore store = new AnalyticsEventStore();
                KafkaEventListener listener = new KafkaEventListener(store);
                if (record != null) {
                    listener.handle(record.value());
                }

                assertThat(store.all()).hasSize(1);
                ProjectEvent stored = store.all().get(0);
                assertThat(stored.getId()).isEqualTo(42L);
                assertThat(stored.getEventType()).isEqualTo("ProjectCreated");
            }
        } finally {
            kafka.stop();
        }
    }
}
