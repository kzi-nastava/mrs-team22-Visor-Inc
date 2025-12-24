package inc.visor.voom_service.person.repository;

import inc.visor.voom_service.person.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
