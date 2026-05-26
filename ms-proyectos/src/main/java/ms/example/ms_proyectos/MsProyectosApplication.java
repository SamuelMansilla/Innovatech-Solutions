package ms.example.ms_proyectos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsProyectosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProyectosApplication.class, args);
	}

}
