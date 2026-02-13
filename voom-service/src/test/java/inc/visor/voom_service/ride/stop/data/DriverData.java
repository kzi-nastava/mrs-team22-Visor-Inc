package inc.visor.voom_service.ride.stop.data;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverData {

    DriverService driverService;

    @Autowired
    public DriverData(DriverService driverService) {
        this.driverService = driverService;
    }

    public class DriverDataBuilder {
        private final Driver driver;

        private DriverDataBuilder() {
            this.driver = new Driver();
        }

        public DriverDataBuilder withUser(User user) {
            driver.setUser(user);
            return this;
        }

        public DriverDataBuilder withStatus(DriverStatus status) {
            driver.setStatus(status);
            return this;
        }

        public Driver save() {
            return driverService.save(driver);
        }
    }
}
