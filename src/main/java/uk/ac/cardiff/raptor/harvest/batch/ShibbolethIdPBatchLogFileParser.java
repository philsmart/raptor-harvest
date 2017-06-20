package uk.ac.cardiff.raptor.harvest.batch;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShibbolethIdPBatchLogFileParser extends BaseBatchLogFileParser {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethIdPBatchLogFileParser.class);

	@PostConstruct
	public void init() {
		log.info("Created Shibboleth IdP BATCH logfile parser, parsing directory [{}]", getBatchDirectory());
	}

}
