package uk.ac.cardiff.raptor.harvest;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.comms.EventPush;
import uk.ac.cardiff.raptor.harvest.enrich.AttributeEnrichment;
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

	@Nullable
	@Inject
	private List<AttributeEnrichment> enrichers;

	/**
	 * Client that pushes {@link Event}s as they are retrieved from the set of
	 * {@link LogParser}s to a suitable endpoint.
	 */
	@Inject
	private EventPush eventPush;

	@PostConstruct
	public void validate() {
		Objects.requireNonNull(eventPush, "Harvester requires an EventPush instance");
		Objects.requireNonNull(parsers, "Harvester MUST be constructer with a list of LogParsers");
		log.info("Harvester has EventPusher [{}]", eventPush.getClass());
		log.info("Harvester has [{}] parsers", parsers.size());
		parsers.forEach(parser -> log.info("Parser [{}]", parser.getName()));
		log.info("Harvester has [{}] enrichers", enrichers != null ? enrichers.size() : "0");
		if (enrichers != null) {
			enrichers.forEach(enricher -> log.info("Enricher [{}]", enricher.getName()));
		}
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
		pushPipeline(allEvents);
	}

	/**
	 * Simple, hard coded, pre-configured, pipeline used when pushing events.
	 * First Enrich {@link Event}s by configured {@code enricher}, then push
	 * events using the configured {@code eventPush}
	 * 
	 * @param events
	 *            the {@link Event}s to push.
	 */
	private void pushPipeline(final List<Event> events) {
		if (enrichers != null) {
			enrichers.forEach(enricher -> enricher.enrich(events));
		}
		eventPush.push(events);
	}

	public EventPush getEventPush() {
		return eventPush;
	}

	public void setEventPush(final EventPush eventPush) {
		this.eventPush = eventPush;
	}

	public List<LogParser> getParsers() {
		return parsers;
	}

	public void setParsers(final List<LogParser> parsers) {
		this.parsers = parsers;
	}

}
