package uk.ac.cardiff.raptor.harvest;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.comms.EventPublisher;
import uk.ac.cardiff.raptor.harvest.enrich.AttributeEnrichment;

/**
 * Simple, hard coded, pre-configured, pipeline used when pushing events. First
 * Enrich {@link Event}s by configured {@code enricher}, then push events using
 * the configured {@code eventPush}
 * 
 * @author philsmart
 *
 */
@Component
@ThreadSafe
public class SimplePushPipeline implements PushPipeline {

	private static final Logger log = LoggerFactory.getLogger(Harvester.class);

	@Nullable
	@Inject
	private List<AttributeEnrichment> enrichers;

	/**
	 * The publisher used to manage the event send lifecycle.
	 */
	@Resource(name = "DefaultEventPublisher")
	private EventPublisher eventPublisher;

	@PostConstruct
	private void validate() {

		log.info("Harvester has [{}] enrichers", enrichers != null ? enrichers.size() : "0");
		if (enrichers != null) {
			enrichers.forEach(enricher -> log.info("Enricher [{}]", enricher.getName()));
		}
	}

	/**
	 * Simple, hard coded, pre-configured, pipeline used when pushing events. First
	 * Enrich {@link Event}s by configured {@code enricher}, then push events using
	 * the configured {@code eventPush}
	 * 
	 * @param events
	 *            the {@link Event}s to push.
	 */
	@Override
	public void pushPipeline(final List<Event> events) {
		if (enrichers != null) {
			enrichers.forEach(enricher -> enricher.enrich(events));
		}
		eventPublisher.push(events);
	}

	/**
	 * @return the eventPublisher
	 */
	public EventPublisher getEventPublisher() {
		return eventPublisher;
	}

	/**
	 * @param eventPublisher
	 *            the eventPublisher to set
	 */
	public void setEventPublisher(final EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	/**
	 * @return the enrichers
	 */
	List<AttributeEnrichment> getEnrichers() {
		return enrichers;
	}

	/**
	 * @param enrichers the enrichers to set
	 */
	void setEnrichers(List<AttributeEnrichment> enrichers) {
		this.enrichers = enrichers;
	}

}
