package ms.example.ms_proyectos.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.utility.DockerImageName;
import org.junit.jupiter.api.Assumptions;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoServiceKafkaIT {

    static KafkaContainer kafka;

    @BeforeAll
    static void startKafka() {
        Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker not available, skipping Kafka IT");
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.1"));
        kafka.start();
    }

    @AfterAll
    static void stopKafka() {
        if (kafka != null) kafka.stop();
    }

    @Test
    void producerSendsMessageToTopic() throws Exception {
        // Configure producer-side KafkaTemplate
        String bootstrap = kafka.getBootstrapServers();

        org.springframework.kafka.core.ProducerFactory<String,String> pf = new org.springframework.kafka.core.DefaultKafkaProducerFactory<>(producerPropsMap(bootstrap));
        org.springframework.kafka.core.KafkaTemplate<String,String> kt = new org.springframework.kafka.core.KafkaTemplate<>(pf);

        // Create producer service
        KafkaProducerService producerService = new KafkaProducerService(kt);

        // Create a sample event map
        java.util.Map<String,Object> e = new java.util.HashMap<>();
        e.put("eventType","ProjectCreated");
        e.put("id",123);
        e.put("nombre","IntegrationTest");

        // Send event
        producerService.sendEvent(e);

        // Create a consumer to read from topic
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "it-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("projects.events"));

        // Poll until message received or timeout
        ConsumerRecord<String,String> record = null;
        long deadline = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < deadline) {
            var recs = consumer.poll(Duration.ofMillis(500));
            if (!recs.isEmpty()) {
                record = recs.iterator().next();
                break;
            }
        }

        consumer.close();

        assertThat(record).isNotNull();
        assertThat(record.value()).contains("ProjectCreated");
        assertThat(record.value()).contains("IntegrationTest");
    }

    private java.util.Map<String,Object> producerPropsMap(String bootstrap) {
        java.util.Map<String,Object> p = new java.util.HashMap<>();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return p;
    }
}
