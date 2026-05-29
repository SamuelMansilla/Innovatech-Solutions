package ms.example.ms_BFF;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsBffApplication.class, args);
	}

}
