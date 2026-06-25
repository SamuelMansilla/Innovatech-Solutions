package ms.example.ms_BFF.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Corregimos la URL para que apunte al puerto 8084 donde ahora vivirá analítica
@FeignClient(name = "analiticaService", url = "http://localhost:8084/api/v1/analytics")
public interface AnaliticaClient {

    @GetMapping("/report")
    String getReport(@RequestParam(name = "type") String type);
}