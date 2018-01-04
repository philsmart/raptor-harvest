package uk.ac.cardiff.raptor.harvest.batch;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BatchParserAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(BatchParserAutoConfiguration.class);

	@Inject
	Environment env;

	@ConditionalOnProperty(prefix = "harvest.shibboleth", name = "batch-directory")
	@ConfigurationProperties(prefix = "harvest.shibboleth")
	@Bean("shibIdPBatchParser")
	public BatchLogFileParserProcessor shibbolethParser() {
		log.info("Creating Shibboleth 3.x IdP Batch Parser");
		final BatchLogFileParserProcessor shibProcessor = new BatchLogFileParserProcessor();
		shibProcessor.setParser(new ShibbolethIdPBatchLogFileParser());
		shibProcessor.setBatchParserName("Shibboleth 3.x IdP Batch File Parser");
		// batch directory is auto configured by Spring.

		return shibProcessor;

	}

	@ConditionalOnProperty(prefix = "harvest.shibbolethv2", name = "batch-directory")
	@ConfigurationProperties(prefix = "harvest.shibbolethv2")
	@Bean("shibIdPBatchParser")
	public BatchLogFileParserProcessor shibbolethParserV2() {
		log.info("Creating Shibboleth 2.x IdP Batch Parser");
		final BatchLogFileParserProcessor shibProcessor = new BatchLogFileParserProcessor();
		shibProcessor.setParser(new ShibbolethIdPBatchLogFileParser());
		shibProcessor.setBatchParserName("Shibboleth 2.x IdP Batch File Parser");
		// batch directory is auto configured by Spring.

		return shibProcessor;

	}

	@ConditionalOnProperty(prefix = "harvest.ezproxy", name = "batch-directory")
	@ConfigurationProperties(prefix = "harvest.ezproxy")
	@Bean("ezproxyBatchParser")
	public BatchLogFileParserProcessor ezproxyParser() {
		log.info("Creating Ezproxy Batch Parser");
		final BatchLogFileParserProcessor ezproxyProcessor = new BatchLogFileParserProcessor();
		ezproxyProcessor.setParser(new EzproxyBatchLogFileParser(env.getProperty("harvest.ezproxy.principal-scope")));
		ezproxyProcessor.setBatchParserName("Ezproxy Batch File Parser");
		// batch directory is auto configured by Spring.

		return ezproxyProcessor;

	}

}
