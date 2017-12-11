package uk.ac.cardiff.raptor.harvest;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.parse.LogParser;

/**
 * <p>
 * Central Harvester class the orchestrates the harvesting of {@link Event}s by
 * using the {@link LogParser}s configured. Parsing is repeated based on a fixed
 * schedule.
 * </p>
 * 
 * @author philsmart
 *
 */
public class Harvester {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(Harvester.class);

	/** List of {@link LogParser}s auto-configured. */
	@Autowired(required = false)
	private List<LogParser> parsers;

	/** The pipeline. */
	@Inject
	private PushPipeline pipeline;

	/**
	 * Validate.
	 */
	@PostConstruct
	public void validate() {

		Objects.requireNonNull(pipeline, "Must have a PushPipeline configured - this should not happen.");
		log.info("Harvester has [{}] parsers", parsers == null ? "0" : parsers.size());
		if (parsers != null) {
			parsers.forEach(parser -> log.info("Parser [{}]", parser.getName()));
		}

	}

	/**
	 * Main harvest method which calls all parsers, gathers all events, then pushes
	 * them through the {@link #pushPipeline(List)} method. Relies on Springs
	 * scheduler. No additional scheduler config, so spring should only ever create
	 * one scheduler thread? - so can assume a single threaded env from this point
	 * (although classes should be threadsafe as far as possible).
	 */
	@Scheduled(initialDelay = 5000, fixedDelay = 50000)
	public void harvest() {
		if (parsers != null) {
			final List<Event> allEvents = parsers.stream().map(LogParser::parse).flatMap(Set::stream)
					.collect(Collectors.toList());

			log.debug("{} Parsers have harvested {} new events", parsers.size(), allEvents.size());
			pipeline.pushPipeline(allEvents);
		}
	}

	/**
	 * Gets the parsers.
	 *
	 * @return the parsers
	 */
	public List<LogParser> getParsers() {
		return parsers;
	}

	/**
	 * Sets the parsers.
	 *
	 * @param parsers
	 *            the new parsers
	 */
	public void setParsers(final List<LogParser> parsers) {
		this.parsers = parsers;
	}

}
