package inc.visor.voom_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VoomServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoomServiceApplication.class, args);
	}

}
