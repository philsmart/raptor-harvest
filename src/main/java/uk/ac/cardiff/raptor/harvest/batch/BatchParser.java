package uk.ac.cardiff.raptor.harvest.batch;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Service class that orchestrates batch file parsing from configured batch
 * directories and associated parsers.
 * 
 * @author philsmart
 *
 */
@Component
public class BatchParser {

	private static final Logger log = LoggerFactory.getLogger(BatchParser.class);

	@Inject
	private final List<BatchLogFileParserProcessor> batchParsers = new ArrayList<BatchLogFileParserProcessor>(0);

	@Scheduled(initialDelay = 5000, fixedDelay = 50000)
	public void batchParse() {
		log.info("Batch parser is starting, has {} parsers", batchParsers.size());

		for (final BatchLogFileParserProcessor parser : batchParsers) {
			parser.parsePush();

		}
	}

}
