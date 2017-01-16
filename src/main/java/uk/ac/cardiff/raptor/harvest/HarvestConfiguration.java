package uk.ac.cardiff.raptor.harvest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import uk.ac.cardiff.raptor.harvest.parse.ParseAutoConfiguration;
import uk.ac.cardiff.raptor.harvest.parse.ShibbolethLogParser;

@Configuration
@Import(ParseAutoConfiguration.class)
public class HarvestConfiguration {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethLogParser.class);

	@Autowired
	private ApplicationContext context;

	@Bean
	public Harvester harvester() {
		log.info("Creating the Harvester....");

		return new Harvester();
	}

}