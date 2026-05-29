package ms.example.ms_talento.repository;

import ms.example.ms_talento.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    // Spring Data JPA ya nos regala el save(), findAll(), findById(), etc.
}