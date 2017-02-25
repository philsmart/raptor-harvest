package uk.ac.cardiff.raptor.harvest.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

public class ParseAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethLogParser.class);

	@ConditionalOnProperty(prefix = "harvest.shibboleth", name = "logfile")
	@ConfigurationProperties(prefix = "harvest.shibboleth")
	@Bean("shibIdpParser")
	public LogParser shibbolethParser() {
		log.info("Creating Shibboleth Idp Log File Parser");
		return new ShibbolethLogParser();
	}

}
