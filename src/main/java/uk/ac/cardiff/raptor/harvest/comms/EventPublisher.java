package uk.ac.cardiff.raptor.harvest.comms;

import java.util.List;

import uk.ac.cardiff.model.event.Event;

/**
 * Interface for an EventPublisher. Implementation is required to be thread safe
 * e.g. by synchronizing the {@link #push(List)} method.
 * 
 * @author philsmart
 *
 */
@FunctionalInterface
public interface EventPublisher {

	/**
	 * Publish the {@link List} of {@link Event}s e.g. to an AMQP exchange, or to a
	 * JDBC connection.
	 * 
	 * @param events
	 */
	void push(List<Event> events);

}