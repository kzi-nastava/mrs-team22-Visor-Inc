package inc.visor.voom_service.domain.repository;

import inc.visor.voom_service.domain.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
