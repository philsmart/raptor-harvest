package uk.ac.cardiff.raptor.harvest.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.PushPipeline;

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
	private final List<BatchLogFileParser> batchParsers = new ArrayList<BatchLogFileParser>(0);

	@Inject
	private PushPipeline pipeline;

	@Scheduled(initialDelay = 5000, fixedDelay = 50000)
	public void batchParse() {
		log.info("Batch parser is starting, has {} parsers", batchParsers.size());

		for (final BatchLogFileParser parser : batchParsers) {
			final Set<Event> events = parser.parse();
			pipeline.pushPipeline(new ArrayList<Event>(events));
		}
	}

}
