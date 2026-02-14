package inc.visor.voom_service.ride.finish;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.person.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User createAndPersistUser(String email) {
        Person person = new Person();
        person.setFirstName("Djordje");
        person.setLastName("Vujanovic");
        person.setPhoneNumber("0655555555");
        person.setAddress("Vase Stajica 16");
        person.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
        entityManager.persist(person);

        UserRole role = new UserRole();
        role.setRoleName("DRIVER");
        entityManager.persist(role);

        User user = new User();
        user.setEmail(email);
        user.setPassword("sifralica123");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setPerson(person);
        user.setUserRole(role);

        return entityManager.persist(user);
    }

    @Test
    @DisplayName("should find driver with correct user id")
    void shouldFindDriverByUserId() {
        User user = createAndPersistUser("drivervozac@gmail.com");

        Driver driver = new Driver();
        driver.setUser(user);
        driver.setStatus(DriverStatus.AVAILABLE);
        entityManager.persist(driver);

        entityManager.flush();
        entityManager.clear();

        Optional<Driver> foundDriver = driverRepository.findByUserId(user.getId());

        assertThat(foundDriver).isPresent();
        assertThat(foundDriver.get().getUser().getEmail()).isEqualTo("drivervozac@gmail.com");
        assertThat(foundDriver.get().getStatus()).isEqualTo(DriverStatus.AVAILABLE);
    }

    @Test
    @DisplayName("should return empty when incorrect use rid")
    void shouldReturnEmptyForNonExistentUserId() {
        Optional<Driver> foundDriver = driverRepository.findByUserId(12092312987L);
        assertThat(foundDriver).isEmpty();
    }

    @Test
    @DisplayName("should correctly update status")
    void shouldUpdateDriverStatusCorrectly() {
        User user = createAndPersistUser("forupdate@gmail.com");
        Driver driver = new Driver();
        driver.setUser(user);
        driver.setStatus(DriverStatus.BUSY);
        driver = entityManager.persistAndFlush(driver);

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
        entityManager.flush();

        Driver updatedDriver = entityManager.find(Driver.class, driver.getId());
        assertThat(updatedDriver.getStatus()).isEqualTo(DriverStatus.AVAILABLE);
    }
}