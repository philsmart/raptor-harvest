package uk.ac.cardiff.raptor.harvest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RaptorHarvestApplication {

	public static void main(final String[] args) {
		SpringApplication.run(RaptorHarvestApplication.class, args);
	}
}
