package uk.ac.cardiff.raptor.harvest.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.ac.cardiff.raptor.harvest.parse.ShibbolethLogParser;

@Configuration
public class BatchParserAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethLogParser.class);

	@ConditionalOnProperty(prefix = "harvest.shibboleth", name = "batch-directory")
	@ConfigurationProperties(prefix = "harvest.shibboleth")
	@Bean("shibIdPBatchParser")
	public BatchLogFileParserProcessor shibbolethParser() {
		log.info("Creating Shibboleth Idp Batch Parser");
		final BatchLogFileParserProcessor shibProcessor = new BatchLogFileParserProcessor();
		shibProcessor.setParser(new ShibbolethIdPBatchLogFileParser());
		shibProcessor.setBatchParserName("Shibboleth IdP Batch File Parser");
		// batch directory is auto configured by Spring.

		return shibProcessor;

	}

}
