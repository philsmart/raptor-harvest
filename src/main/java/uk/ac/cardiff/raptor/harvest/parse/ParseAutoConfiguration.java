package uk.ac.cardiff.raptor.harvest.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

public class ParseAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethLogParser.class);

	@ConditionalOnProperty(prefix = "harvest.shibbolethv2", name = "logfile")
	@ConfigurationProperties(prefix = "harvest.shibbolethv2")
	@Bean("shibIdpParser")
	public LogParser shibbolethParser() {

		return LogParserFactory.newShibbolethLogParser();
	}

	@ConditionalOnProperty(prefix = "harvest.shibboleth", name = "logfile")
	@ConfigurationProperties(prefix = "harvest.shibboleth")
	@Bean("shibIdpParser")
	public LogParser shibbolethV3Parser() {

		return LogParserFactory.newShibbolethV3LogParser();
	}

	@ConditionalOnProperty(prefix = "harvest.ezproxy", name = "logfile")
	@ConfigurationProperties(prefix = "harvest.ezproxy")
	@Bean("ezproxyParser")
	public LogParser ezproxyParser() {
		// null principal scope as this will be auto-configured by spring.
		return LogParserFactory.newEzproxyLogFileParser(null);
	}

}
