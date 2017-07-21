package uk.ac.cardiff.raptor.harvest.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchParserAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(BatchParserAutoConfiguration.class);

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

	@ConditionalOnProperty(prefix = "harvest.ezproxy", name = "batch-directory")
	@ConfigurationProperties(prefix = "harvest.ezproxy")
	@Bean("ezproxyBatchParser")
	public BatchLogFileParserProcessor ezproxyParser() {
		log.info("Creating Ezproxy Batch Parser");
		final BatchLogFileParserProcessor ezproxyProcessor = new BatchLogFileParserProcessor();
		ezproxyProcessor.setParser(new EzproxyBatchLogFileParser());
		ezproxyProcessor.setBatchParserName("Ezproxy Batch File Parser");
		// batch directory is auto configured by Spring.

		return ezproxyProcessor;

	}

}
