package uk.ac.cardiff.raptor.harvest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import uk.ac.cardiff.raptor.harvest.parse.LogParser;
import uk.ac.cardiff.raptor.harvest.parse.ParseAutoConfiguration;
import uk.ac.cardiff.raptor.harvest.parse.ShibbolethLogParser;

/**
 * <p>
 * Basic configuration of a {@link Harvester}. Occurs after
 * {@link ParseAutoConfiguration} so that that harvester can have
 * {@link LogParser}s injected.
 * </p>
 * 
 *
 */
@Configuration
@Import(ParseAutoConfiguration.class)
public class HarvestConfiguration {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethLogParser.class);

	@Bean
	public Harvester harvester() {
		return new Harvester();
	}

}
