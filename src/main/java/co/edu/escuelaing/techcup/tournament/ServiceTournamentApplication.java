package co.edu.escuelaing.techcup.tournament;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServiceTournamentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceTournamentApplication.class, args);
	}

}
