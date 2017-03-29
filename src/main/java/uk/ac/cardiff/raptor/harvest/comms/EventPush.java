package uk.ac.cardiff.raptor.harvest.comms;

import java.util.List;

import javax.annotation.Nonnull;

import uk.ac.cardiff.model.event.Event;

/**
 * Push {@link Event}s in some manor (left to the implementation) to a server.
 * 
 * @author philsmart
 *
 */
public interface EventPush {

	/**
	 * Push the events using a suitable implementation e.g. AMQP
	 * 
	 * @param events
	 *            the {@link Event}s to push.
	 * @return any {@link Event}s that failed to send, should not be null, can
	 *         be empty.
	 */
	@Nonnull
	public List<Event> push(List<Event> events);

}
