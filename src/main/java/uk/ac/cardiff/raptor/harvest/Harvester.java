package uk.ac.cardiff.raptor.harvest;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.parse.LogParser;

/**
 * Central Harvester class the orchestrates the {@link LogParser}s configured.
 * 
 * @author philsmart
 *
 */
public class Harvester {

	private static final Logger log = LoggerFactory.getLogger(Harvester.class);

	/**
	 * List of {@link LogParser}s auto-configured
	 */
	@Inject
	private List<LogParser> parsers;

	@Inject
	private PushPipeline pipeline;

	@PostConstruct
	public void validate() {

		Objects.requireNonNull(parsers, "Harvester MUST be constructer with a list of LogParsers");
		Objects.requireNonNull(pipeline, "Must have a PushPipeline configured - this should not happen.");
		log.info("Harvester has [{}] parsers", parsers.size());
		parsers.forEach(parser -> log.info("Parser [{}]", parser.getName()));

	}

	/**
	 * Main harvest method which calls all parsers, gathers all the events, then
	 * pushes them through the {@link #pushPipeline(List)} method. Relies on
	 * Springs scheduler. No additional scheduler config, so spring should only
	 * ever create one scheduler thread? - so can assume a single threaded env
	 * from this point (although classes should be threadsafe as far as
	 * possible).
	 */
	@Scheduled(initialDelay = 5000, fixedDelay = 50000)
	public void harvest() {
		final List<Event> allEvents = parsers.stream().map(LogParser::parse).flatMap(Set::stream)
				.collect(Collectors.toList());

		log.debug("Has harvested {} new events", allEvents.size());
		pipeline.pushPipeline(allEvents);
	}

	public List<LogParser> getParsers() {
		return parsers;
	}

	public void setParsers(final List<LogParser> parsers) {
		this.parsers = parsers;
	}

}
