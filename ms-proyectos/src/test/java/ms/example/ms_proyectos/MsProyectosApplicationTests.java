package ms.example.ms_proyectos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
class MsProyectosApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		public ProducerFactory<String, String> producerFactory() {
			Map<String, Object> props = new HashMap<>();
			props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
			props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
			props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
			return new DefaultKafkaProducerFactory<>(props);
		}

		@Bean
		public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
			return new KafkaTemplate<>(pf);
		}
	}

	@Test
	void contextLoads() {
	}

}
