package uk.ac.cardiff.raptor.harvest.comms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hibernate.annotations.Synchronize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.parse.LogParser;

@Component("DefaultEventPublisher")
public class EventPublisher {

	private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

	/**
	 * Client that pushes {@link Event}s as they are retrieved from the set of
	 * {@link LogParser}s to a suitable endpoint.
	 */
	@Inject
	private EventPush eventPush;

	/**
	 * A queue of failed events.These should be retried.
	 */
	private final List<Event> failedEventsQueue = new ArrayList<Event>();

	@PostConstruct
	public void init() {
		Objects.requireNonNull(eventPush, "Harvester requires an EventPush instance");
		log.info("Harvester has EventPusher [{}]", eventPush.getClass());

	}

	/**
	 * Push the {@link List} of events to the configured {@link EventPush}
	 * interface. Handles the push life-cycle. Keeps record of any failures for
	 * retry. This method is {@link Synchronize}d in case it is called from
	 * multiple threads - this is to prevents concurrent access to the
	 * {@code failedEventsQueue}.
	 * 
	 * @param events
	 *            the {@link Event}s to send.
	 */
	// TODO where is retry?
	public synchronized void push(final List<Event> events) {

		final List<Event> failures = eventPush.push(events);
		if (failures.isEmpty() == false) {
			failedEventsQueue.addAll(failures);
		}
		log.info("EventPush had {} events to push, {} of them failed, failed events queue now has {} events",
				events.size(), failures.size(), failedEventsQueue.size());

	}

}
