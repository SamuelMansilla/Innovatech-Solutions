package ms.example.ms_BFF.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Le decimos a Feign que se conecte al puerto 8083 donde vive ms-analitica
@FeignClient(name = "analiticaService", url = "http://localhost:8083/api/v1/analytics")
public interface AnaliticaClient {

    // Este método es un "espejo" del controlador que hicimos en ms-analitica
    @GetMapping("/report")
    String getReport(@RequestParam(name = "type") String type);
}