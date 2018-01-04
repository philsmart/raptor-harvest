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

	/**
	 * <p>
	 * Calls the {@link BatchLogFileParserProcessor#parsePush()} method of all
	 * configured {@code batchParsers}.
	 * </p>
	 * 
	 * <p>
	 * This is a scheduled task, executes 5000ms after startup, then the value of
	 * the property {@code batch.general.rate-ms}ms thereafter.
	 * </p>
	 */
	@Scheduled(initialDelay = 3000, fixedDelayString = "${batch.general.rate-ms}")
	public void batchParse() {
		log.info("Batch parser is starting, has {} parsers [{}]", batchParsers.size(), batchParsers);

		for (final BatchLogFileParserProcessor parser : batchParsers) {
			parser.parsePush();

		}
	}

}
