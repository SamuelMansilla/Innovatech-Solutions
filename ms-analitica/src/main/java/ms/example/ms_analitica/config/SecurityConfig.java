package ms.example.ms_analitica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitamos CSRF porque es una API REST y no usa formularios web
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // Permitimos el acceso libre a nuestros endpoints de analítica
                .requestMatchers("/api/v1/analytics/**").permitAll() 
                // Cualquier otra petición (como actuator) requerirá autenticación
                .anyRequest().authenticated()
            );
            
        return http.build();
    }
}